package net.neogamesmc.common.redis;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Singleton;
import net.neogamesmc.common.redis.api.Focus;
import net.neogamesmc.common.redis.api.FromChannel;
import net.neogamesmc.common.redis.api.HandlesType;
import net.neogamesmc.common.redis.api.Payload;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.*;

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
    private static final boolean DEBUG_ENABLED = Boolean.valueOf(System.getProperty("net.neogamesmc.common.redis.debug", "false"));

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
            debug("Fetching focus for payload type: " + key.getName());

            return key.getAnnotation(Focus.class).value();
        }
    });

    /** a pool of redis connections */
    private JedisPool pool;

    /** one {@link Jedis} instance dedicated to the thread-blocking op of "subbing" to channels */
    private volatile Jedis subscriber;

    /** collection of hooks to our redis system */
    private ConcurrentHashMap<String, HookData> hooks;

    /** run redis requests async */
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
     * Disconnect all of our stuff
     *
     * @return this handler
     */
    public RedisHandler release()
    {
        pool.close();
        executor.shutdown();
        subscriber.close();

        return this;
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
        final String[] _channelsAsString = new String[channels.length];

        for (int i = 0; i < channels.length; i++)
            _channelsAsString[i] = channels[i].channel;

        new Thread("Hyleria Redis Pub/Sub")
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
                            debug("Received JSON message on channel: " + channel);
                            debug("Message: [" + message + "]");

                            final JsonObject json = JSON_PARSER.parse(message).getAsJsonObject();
                            final String focus = json.get("focus").getAsString();

                            if (hooks.containsKey(focus))
                            {
                                final HookData data = hooks.get(focus);

                                if (data.channel.equals(channel))
                                {
                                    data.method.invoke(data.possessor, GSON.fromJson(json.has("payload")
                                                                                       ? json.get("payload").toString()
                                                                                       : "", data.payloadType));
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
                }, _channelsAsString);
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
            debug("Publishing payload on " + channel + "\npayload w/o focus: [" + payload.asJSON().toString() + "]");

            try (Jedis jedis = pool.getResource())
            {
                jedis.publish(channel, payload.asString(payloadFocusCache.get(payload.getClass())));
            }
            catch (ExecutionException ex)
            {
                ex.printStackTrace();
                System.err.println("Issue fetching focus for payload: " + payload.getClass().getName());
                System.err.println();
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
            // lazy init
            if (hooks == null)
                hooks = new ConcurrentHashMap<>();

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
    private static void debug(String message)
    {
        if (DEBUG_ENABLED)
            System.out.println("[Redis Debug] " + message);
    }

    /**
     * Set of data pertaining to a Redis hook
     */
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
