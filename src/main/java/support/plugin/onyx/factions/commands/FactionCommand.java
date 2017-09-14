package support.plugin.onyx.factions.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import support.plugin.onyx.Onyx;
import support.plugin.onyx.commands.handler.ICommand;
import support.plugin.onyx.commands.handler.Info;
import support.plugin.onyx.config.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base faction command, handles all other executors
 */
public class FactionCommand implements CommandExecutor {

    private Onyx instance;

    private Set<ICommand> commands;

    public FactionCommand(Onyx instance) {

        this.instance = instance;

        commands = new HashSet<>();

        loadCommands();
    }

    /**
     * Loads all faction sub-commands
     */
    private void loadCommands() {

        commands.add(new FactionCreateCommand(instance));
        commands.add(new FactionDisbandCommand(instance));
        commands.add(new FactionInviteCommand(instance));
        commands.add(new FactionJoinCommand(instance));
        commands.add(new FactionUninviteCommand(instance));

    }

    /**
     * The main executor for the sub-commands
     *
     * @param sender the command sender
     * @param cmd the command
     * @param commandLabel the command label
     * @param args the command arguments
     * @return success boolean
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        Configuration locale = instance.getLocale();

        if (!(sender instanceof Player)) {

            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("commands.console_sender")));
            return true;

        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("factions")) {

            if (args.length == 0) {

                if (locale.getBoolean("general.help.breakers_enabled")) {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("general.help.breaker")));

                }

                for (ICommand command : commands) {

                    Info info = command.getClass().getAnnotation(Info.class);

                    String commandFormat = locale.getString("general.help.command_usage");

                    commandFormat = commandFormat.replace("{command}", info.subCommand());
                    commandFormat = commandFormat.replace("{usage}", info.usage());
                    commandFormat = commandFormat.replace("{description}", info.description());

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', commandFormat));

                }

                if (locale.getBoolean("general.help.breakers_enabled")) {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("general.help.breaker")));

                }

                return true;

            }

            ICommand commandWanted = null;

            for (ICommand command : commands) {

                Info info = command.getClass().getAnnotation(Info.class);

                if (info.subCommand().equalsIgnoreCase(args[0])) {

                    commandWanted = command;
                    break;

                }

                for (String alias : info.aliases()) {

                    if (alias.equalsIgnoreCase(args[0])) {
                        commandWanted = command;
                        break;
                    }

                }

            }

            if (commandWanted == null) {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("commands.invalid_command")));
                return true;

            }

            if (!(player.hasPermission(commandWanted.getClass().getAnnotation(Info.class).permission()))) {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("general.no_permission")));
                return true;

            }

            Set<String> newArgs = new HashSet<>();
            Collections.addAll(newArgs, args);
            newArgs.remove(0);
            args = newArgs.toArray(new String[newArgs.size()]);

            commandWanted.execute(player, args);

        }

        return true;
    }

}
