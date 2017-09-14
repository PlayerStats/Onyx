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

/*
Copyright (c) 2017 PluginManager LTD. All rights reserved.
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge and/or publish copies of the Software,
and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
Any copies of the Software shall stay private and cannot be resold.
Credit to PluginManager LTD shall be expressed in all forms of advertisement and/or endorsement.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

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
        commands.add(new FactionChatCommand(instance));

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
