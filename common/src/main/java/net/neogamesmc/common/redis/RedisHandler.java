package net.neogamesmc.common.redis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Singleton;
import lombok.EqualsAndHashCode;
import lombok.val;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.redis.api.Payload;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Expose an interface to work with Redis.
 *
 * @author Ben (OutdatedVersion)
 * @since Mar/24/2017 (11:57 AM)
 */
@Singleton
public class RedisHandler
{

    /** Whether or not to print out debug messages here */
    private static final boolean DEBUG_ENABLED = Boolean.getBoolean("net.neogamesmc.common.redis.debug");

    /** Convert raw JSON strings into Java objects */
    private static final JsonParser JSON_PARSER = new JsonParser();

    /** JSON <-> Java object */
    private static final Gson GSON = new Gson();

    /** Load the focus from the provided payload class | cache due to the basic reflection present */
    private LoadingCache<Class<? extends Payload>, String> payloadFocusCache = CacheBuilder.newBuilder()
            .expireAfterAccess(4, TimeUnit.HOURS)
            .build(new CacheLoader<Class<? extends Payload>, String>()
    {
        @Override
        public String load(Class<? extends Payload> key) throws Exception
        {
            checkState(key.isAnnotationPresent(Focus.class), "Invalid payload! Missing Focus annotation.");
            debug(() -> "Fetching focus for payload type: " + key.getName());

            return key.getAnnotation(Focus.class).value();
        }
    });

    /**
     * A thread-safe pool supplying {@link Jedis} instances for interaction with Redis.
     */
    private JedisPool pool;

    /**
     * One {@link Jedis} instance reserved to perform the thread-blocking
     * operation of "subscribing" to a Redis pub/sub channel.
     */
    private volatile Jedis subscriber;

    /**
     * Usage hooks for our Redis system.
     * <p>
     * Not thread-safe
     */
    private Multimap<String, HookData> hooks;

    /**
     * Run requests asynchronously.
     */
    private ExecutorService executor;

    /**
     * Start up our connection pool
     *
     * @return this handler
     */
    public RedisHandler init()
    {
        pool = new JedisPool();
        executor = Executors.newCachedThreadPool();

        return this;
    }

    /**
     * Disconnect all of our stuff.
     */
    public void release()
    {
        pool.close();
        executor.shutdown();
        subscriber.close();
    }

    /**
     * Grab a (R/)Jedis connection from our pool.
     *
     * @return The client
     */
    public Jedis client()
    {
        return pool.getResource();
    }

    /**
     * Start receiving data from the provided
     * Redis channels
     *
     * @param channels the channels to listen to
     * @return this handler
     */
    public RedisHandler subscribe(final RedisChannel... channels)
    {
        readyCheck("incoming data handler setup");

        // we need the channels we're subbing to as Strings so
        final String[] channelsAsString = new String[channels.length];

        for (int i = 0; i < channels.length; i++)
            channelsAsString[i] = channels[i].channel;

        new Thread("NeoGames Redis Pub/Sub")
        {
            @Override
            public void run()
            {
                subscriber = pool.getResource();
                subscriber.connect();

                subscriber.subscribe(new JedisPubSub()
                {
                    @Override
                    public void onMessage(String channel, String message)
                    {
                        try
                        {
                            debug(() -> "Received JSON message on channel: " + channel);
                            debug(() -> "Message: [" + message + "]");

                            val json = JSON_PARSER.parse(message).getAsJsonObject();
                            val focus = json.get("focus").getAsString();

                            if (hooks.containsKey(focus))
                            {
                                for (HookData data : hooks.get(focus))
                                {
                                    if (data.channel.equals(channel))
                                    {
                                        data.method.invoke(data.possessor, GSON.fromJson(json.has("payload")
                                                                                         ? json.get("payload").toString()
                                                                                         : "", data.payloadType));
                                    }
                                }
                            }
                        }
                        catch (JsonSyntaxException ex)
                        {
                            System.err.println("Invalid JSON provided to Redis system");
                            System.err.println("Verify this payload is correct:");
                            System.err.println(message);
                            System.err.println();
                        }
                        catch (IllegalAccessException | InvocationTargetException ex)
                        {
                            ex.printStackTrace();
                            System.err.println("Unable to manually invoke the method behind the provided Redis hook");
                            System.err.println();
                        }
                    }
                }, channelsAsString);
            }
        }.start();

        return this;
    }

    /**
     * Publish the provided payload.
     *
     * @param payload The payload
     * @return This handler, for chaining
     */
    public RedisHandler publish(Payload payload)
    {
        publish(payload.channel().channel, payload);
        return this;
    }

    /**
     * @param channel the {@link RedisChannel} we're sending it to
     * @param payload our payload
     * @return our redis instance
     *
     * @see #publish(String, Payload)
     */
    public RedisHandler publish(RedisChannel channel, Payload payload)
    {
        return publish(channel.channel, payload);
    }

    /**
     * Send out a payload across our
     * Redis instance(s).
     *
     * @param channel the channel to send it over
     * @param payload what we're sending
     * @return this handler
     */
    public RedisHandler publish(String channel, Payload payload)
    {
        readyCheck("outgoing payload request");

        executor.submit(() ->
        {
            debug(() -> "Publishing payload on " + channel + "\npayload w/o focus: [" + GSON.toJson(payload) + "]");

            try (Jedis jedis = pool.getResource())
            {
                val json = new JSONObject();

                json.put("focus", payloadFocusCache.get(payload.getClass()));

                if (payload.hasContent())
                {
                    json.put("payload", GSON.toJsonTree(payload).getAsJsonObject());
                }

                jedis.publish(channel, json.toJSONString());
            }
            catch (ExecutionException ex)
            {
                ex.printStackTrace();
                System.err.println("Issue fetching focus for payload: " + payload.getClass().getName());
                System.err.println();
            }
            catch (Exception ex)
            {
                throw new RuntimeException("Unknown issue occurred whilst publishing Redis payload.", ex);
            }
        });

        return this;
    }

    /**
     * Take in the provided hook, and make sure
     * this handler starts to take it into
     * consideration whilst processing payloads.
     *
     * @param object what you'd like to register
     * @return this handler
     */
    public RedisHandler registerHook(Object object)
    {
        try
        {
            debug(() -> "Provisioning hook at: " + object.getClass().getCanonicalName());

            // lazy init
            if (hooks == null)
                hooks = MultimapBuilder.hashKeys().hashSetValues().build();

            boolean provisionedHook = false;

            for (Method method : object.getClass().getMethods())
            {
                // every "virtual hook" must have one of these
                if (method.isAnnotationPresent(HandlesType.class))
                {
                    checkState(method.getParameterCount() == 1, "We only invoke with the provided payload; nothing else!");
                    checkState(Payload.class.isAssignableFrom(method.getParameterTypes()[0]), "The provided parameter isn't a payload!");

                    final HookData data = new HookData();

                    data.possessor = object;
                    data.method = method;
                    data.channel = method.isAnnotationPresent(FromChannel.class) ? method.getAnnotation(FromChannel.class).value().channel : RedisChannel.DEFAULT.channel;
                    data.focus = payloadFocusCache.get(data.payloadType = method.getAnnotation(HandlesType.class).value());

                    hooks.put(data.focus, data);

                    provisionedHook = true;
                }
            }

            // in case someone screwed up
            checkState(provisionedHook, "A method conforming to the standards of our hooks has not been found in [" + object.getClass().getName() + "]. please fix!!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return this;
    }

    /**
     * Verifies that this handler has been
     * started up before performing the
     * specified task.
     *
     * @param operation whatever we're doing;
     *                  allows for slightly more
     *                  useful exception message.
     */
    private void readyCheck(String operation)
    {
        checkNotNull(pool, "Be sure to call RedisHandler#init before performing: [" + operation + "]");
    }

    /**
     * Prints the provided message
     * only if debug is enabled here
     *
     * @param message the message
     */
    private static void debug(Supplier<String> message)
    {
        if (DEBUG_ENABLED)
            System.out.println("[Redis Debug] " + message.get());
    }

    /**
     * Set of data pertaining to a Redis hook
     */
    @EqualsAndHashCode
    private static class HookData
    {
        /** instance of the item holding this hook */
        Object possessor;

        /** Java method backing this hook */
        Method method;

        /** the redis channel we're looking for */
        String channel;

        /** the intent of the payload for this hook */
        String focus;

        /** the payload type this hook is processing */
        Class<? extends Payload> payloadType;
    }

}
