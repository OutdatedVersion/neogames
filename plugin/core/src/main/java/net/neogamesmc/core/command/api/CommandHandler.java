package net.neogamesmc.core.command.api;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.neogamesmc.common.database.Database;
import net.neogamesmc.common.reference.Role;
import net.neogamesmc.core.bukkit.Plugin;
import net.neogamesmc.core.command.api.annotation.Command;
import net.neogamesmc.core.command.api.annotation.Necessary;
import net.neogamesmc.core.command.api.annotation.Permission;
import net.neogamesmc.core.command.api.annotation.SubCommand;
import net.neogamesmc.core.command.api.satisfier.IntegerSatisfier;
import net.neogamesmc.core.command.api.satisfier.PlayerSatisfier;
import net.neogamesmc.core.command.api.satisfier.RoleSatisfier;
import net.neogamesmc.core.command.api.satisfier.StringArraySatisfier;
import net.neogamesmc.core.issue.Issues;
import net.neogamesmc.core.message.Message;
import net.neogamesmc.core.message.Messages;
import net.neogamesmc.core.message.option.format.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static net.md_5.bungee.api.ChatColor.GRAY;
import static net.md_5.bungee.api.ChatColor.YELLOW;
import static net.neogamesmc.core.message.Message.prefix;

/**
 * @author Ben (OutdatedVersion)
 * @since Feb/28/2017 (5:50 PM)
 */
@Singleton
public class CommandHandler implements Listener
{

    /** the message sent when someone runs a command that doesn't exist */
    private static final BaseComponent[] HELP_MESSAGE = new ComponentBuilder("Unknown command... we're here to ")
                                                            .color(GRAY).bold(true).append("/help").color(YELLOW)
                                                            .append(" you.").color(GRAY).create();

    /**
     * A set of {@link ArgumentSatisfier}s that a large
     * majority of our commands require.
     */
    public static final Collection<Class<? extends ArgumentSatisfier>> DEFAULT_PROVIDERS = Lists.newArrayList(
            RoleSatisfier.class, PlayerSatisfier.class, StringArraySatisfier.class, IntegerSatisfier.class
    );

    /** Default commands that we won't let players run unless we have one that overrides it */
    private static final Set<String> BLOCKED_COMMANDS = Sets.newHashSet(
            "help", "about", "ver", "version"
    );

    /**
     * An instance of one of our plugins held
     * over a layer of abstraction.
     */
    @Inject private Plugin plugin;

    /**
     * Interact with player data.
     */
    @Inject private Database database;

    /**
     * A relation of our commands
     */
    private Map<String, BaseCommandInfo> commands = Maps.newHashMap();

    /** what we use to satisfy args */
    private Map<Class, ArgumentSatisfier> providers = Maps.newHashMap();

    /**
     * @param satisfiers a collection of providers to register
     * @return this handler
     */
    public CommandHandler addProviders(Collection<Class<? extends ArgumentSatisfier>> satisfiers)
    {
        satisfiers.forEach(this::addProvider);
        return this;
    }

    /**
     * @param satisfier logic for the provider
     * @param <T> type of provider
     * @return this handler
     */
    public <T> CommandHandler addProvider(ArgumentSatisfier<T> satisfier)
    {
        providers.put(satisfier.satisfies(), satisfier);
        return this;
    }

    /**
     * @param satisfierClass class of some satisifier
     * @return this handler
     */
    @SuppressWarnings ( "unchecked" )
    public CommandHandler addProvider(Class<? extends ArgumentSatisfier> satisfierClass)
    {
        addProvider(plugin.get(satisfierClass));
        return this;
    }

    /**
     * Looks over the classes in the
     * provided package, and if methods
     * exist in it annotated with our
     * command annotation, we'll process
     * those.
     *
     * @param packages Multiple fully qualified
     *                 package names.
     */
    public void registerInPackage(String... packages)
    {
        new FastClasspathScanner(packages)
                .addClassLoader(plugin.getClass().getClassLoader())
                .matchClassesWithMethodAnnotation(Command.class, (clazz, method) ->
                                registerObject(plugin.get(clazz))).scan();
    }

    /**
     * Register all of the provided classes
     * to our handler
     *
     * @param classes the classes
     * @return this handler
     */
    public CommandHandler register(Class<?>... classes)
    {
        for (Class clazz : classes)
            registerObject(plugin.get(clazz));

        return this;
    }

    /**
     * Look over the provided object
     * and register any commands in it
     * to our handler.
     *
     * @param object the object
     * @return this handler
     */
    public CommandHandler registerObject(Object object)
    {
        // Iterate over a class looking for a method suitable
        // for being a used as a command. Keep in mind, only
        // public methods are eligible

        final List<Method> toProcess = Stream.of(object.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Command.class) || method.isAnnotationPresent(SubCommand.class))
                .sorted((one, two) ->
        {
            if (one.isAnnotationPresent(Command.class) && two.isAnnotationPresent(SubCommand.class))
                return -1;

            return 0;
        }).collect(Collectors.toList());


        for (Method method : toProcess)
        {
            if (method.isAnnotationPresent(Command.class))
            {
                final Command ann = method.getAnnotation(Command.class);

                checkState(method.getParameterTypes()[0].equals(Player.class),
                           "The first argument in any command must be a Player (the one who executed the command)");

                final BaseCommandInfo info = new BaseCommandInfo();

                info.method = method;
                info.executors = Sets.newHashSet(ann.executor());
                permissionDataFromMethod(method, info);

                info.possessor = object;

                for (String executor : info.executors)
                    commands.put(executor.toLowerCase(), info);
            }

            if (method.isAnnotationPresent(SubCommand.class))
            {
                final SubCommand annotation = method.getAnnotation(SubCommand.class);

                checkState(method.getParameterTypes()[0].equals(Player.class), "Missing player arg in pos 1 (0)");
                checkState(commands.containsKey(annotation.of()), "Parent command must be registered first!");

                final SubCommandInfo info = new SubCommandInfo();

                info.executors = Sets.newHashSet(annotation.executors());
                info.method = method;
                info.possessor = object;
                permissionDataFromMethod(method, info);

                commands.get(annotation.of()).addSubCommand(info);
            }
        }

        return this;
    }

    @EventHandler ( priority = EventPriority.MONITOR, ignoreCancelled = true )
    public void interceptCommands(PlayerCommandPreprocessEvent event)
    {
        final String[] split = event.getMessage().split(" ");
        final String command = split[0].substring(1).toLowerCase();

        final BaseCommandInfo info = commands.get(command);

        if (info != null)
        {
            final String[] args = new String[split.length - 1];
            System.arraycopy(split, 1, args, 0, args.length);

            // these are my least favorite things to do
            final CommandInfo[] invokeInfo = { info };

            if (args.length >= 1 && info.subCommands != null)
            {
                info.subCommands.stream()
                                .filter(s -> s.executors.contains(args[0]))
                                .findFirst()
                                .ifPresent(s -> invokeInfo[0] = s);
            }

            attemptCommandExecution(invokeInfo[0], invokeInfo[0].method, event.getPlayer(), invokeInfo[0] instanceof SubCommandInfo ? Arrays.copyOfRange(args, 1, args.length)
                                                                                                                                       : args);

            event.setCancelled(true);
            return;
        }
        else if (BLOCKED_COMMANDS.contains(command))
        {
            event.getPlayer().sendMessage(HELP_MESSAGE);
            event.setCancelled(true);
        }
        else if (!event.getPlayer().hasPermission("role.admin") && !event.getPlayer().isOp())
        {
            event.getPlayer().sendMessage(HELP_MESSAGE);
            event.setCancelled(true);
        }


        if (!event.isCancelled() && plugin.getServer().getHelpMap().getHelpTopic("/" + command) == null)
        {
            event.getPlayer().sendMessage(HELP_MESSAGE);
            event.setCancelled(true);
        }
    }

    /**
     * @param info info
     * @param method the method
     * @param player the player
     * @param rawArguments what the player typed
     */
    private void attemptCommandExecution(CommandInfo info, Method method, Player player, String[] rawArguments)
    {
        try
        {
            // verify the player can actually execute this command
            if (info.role != Role.PLAYER)
            {
                val account = database.cacheFetch(player.getUniqueId());

                if (!account.role().compare(info.role))
                {
                    info.permissionMessage.sendAsIs(player);
                    return;
                }
            }

            // prepare parameters
            final Arguments args = new Arguments(rawArguments);
            final Parameter[] required = method.getParameters();
            final Object[] invokingWith = new Object[required.length];

            // the player who ran the command is always the first parameter
            invokingWith[0] = player;

            // let's start satisfying each parameter
            for (int i = 1; i < required.length; i++)
            {
                final Parameter working = required[i];

                ArgumentSatisfier provider;

                // make sure if we need something & it doesn't exist that we fail
                final Necessary necessary = working.getDeclaredAnnotation(Necessary.class);

                if (args.remainingElements() == 0)
                {
                    if (necessary != null)
                    {
                        prefix("Commands").content(necessary.value(), Color.RED).send(player);
                        return;
                    }
                    else
                    {
                        invokingWith[i] = null;
                        continue;
                    }
                }

                // if there's an annotation present we'll handle
                // it based on the recommendation of that provider
                // instead of the type of the parameter
                if (working.getDeclaredAnnotations().length > 0 && working.getDeclaredAnnotations()[0].annotationType() != Necessary.class)
                {
                    // TODO(Ben): allow for more versatile annotations. so they can do different things based on the type of the parameter
                    // only one deciding annotation allowed by parameter
                    // this does not include the "necessary" annotation

                    provider = providers.get(working.getDeclaredAnnotations()[0].annotationType());
                }
                else
                {
                    provider = providers.get(working.getType());
                }

                // we should always have some sort of provider available
                if (provider != null)
                {
                    final Arguments copy = args.clone();

                    invokingWith[i] = provider.get(player, args);

                    if (invokingWith[i] == null)
                    {
                        val next = copy.next();
                        val fail = provider.fail(next);

                        if (fail != null)
                            Message.prefix("Commands").content(fail, Color.RED).send(player);

                        return;
                    }
                }
                // in the case there isn't a provider, let's just try providing a String
                else if (working.getType().equals(String.class))
                {
                    invokingWith[i] = args.next();
                }
                // nothing we can do now, fail
                else throw new IllegalArgumentException("Missing provider for parameter type: " + working.getType().getName());
            }


            // we've figured out our parameters; execute the command now
            method.invoke(info.possessor, invokingWith);
        }
        catch (Exception ex)
        {
            Issues.handle("Command Execution", ex);
        }
    }

    /**
     * @param method the method we're scanning
     * @param info the info we'll be assigning the found data to
     */
    private static void permissionDataFromMethod(Method method, CommandInfo info)
    {
        final Permission perm = method.getAnnotation(Permission.class);

        if (perm != null)
        {
            info.role = perm.value();
            info.permissionMessage = perm.note().equals("DEFAULT_MESSAGE")
                                     ? Messages.PERMISSION
                                     : prefix("Permissions").content(perm.note(), Color.RED);
        }
        else
        {
            info.role = Role.PLAYER;
            info.permissionMessage = Messages.PERMISSION;
        }
    }

    /**
     * Send's our "unknown command" message to
     * the provided player
     *
     * @param player the target player
     */
    public static void sendHelpMessage(Player player)
    {
        player.sendMessage(HELP_MESSAGE);
    }

    /**
     * Common data shared between (sub-)commands.
     */
    static abstract class CommandInfo
    {
        /** an instance of where command method resides */
        Object possessor;

        /** the method for this command */
        Method method;

        /** what someone may type to run this command */
        Set<String> executors;

        /** The role a player must hold to run this command */
        Role role;

        /** Message to send the player if we are unable to execute the command */
        Message permissionMessage;
    }

    /**
     * Data behind a command.
     */
    static class BaseCommandInfo extends CommandInfo
    {
        /** the sub-commands for this command */
        Set<SubCommandInfo> subCommands;

        /**
         * @param info the sub-command to add
         */
        void addSubCommand(SubCommandInfo info)
        {
            if (subCommands == null)
                subCommands = Sets.newHashSet();

            subCommands.add(info);
        }
    }

    /**
     * Data for a sub-command of a {@link BaseCommandInfo}.
     */
    static class SubCommandInfo extends CommandInfo { }

}
