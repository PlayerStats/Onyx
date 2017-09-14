package support.plugin.onyx.factions.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import support.plugin.onyx.Onyx;
import support.plugin.onyx.commands.handler.ICommand;
import support.plugin.onyx.commands.handler.Info;
import support.plugin.onyx.config.Configuration;
import support.plugin.onyx.factions.Faction;
import support.plugin.onyx.factions.FactionManager;
import support.plugin.onyx.factions.enums.FactionRole;

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
 * Handles the sub-command for joining factions
 */
@Info(subCommand = "join", description = "Join a faction", usage = "<faction> (--force)", permission = "onyx.factions.join", aliases = {"join", "j"})
public class FactionJoinCommand implements ICommand {

    private Onyx instance;

    public FactionJoinCommand(Onyx instance) {
        this.instance = instance;
    }

    @Override
    public void execute(Player player, String[] args) {

        Configuration locale = instance.getLocale();
        FactionManager factionManager = instance.getFactionManager();

        if (factionManager.getFactionByMember(player.getUniqueId()) == null) {

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("faction.general.not_in_faction")));
            return;

        }

        if (args.length == 0) {

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("general.invalid_arguments") + " &c/faction join <faction/player>"));
            return;

        }

        Faction faction = factionManager.getFactionByMember(player.getUniqueId());

        if (faction != null) {

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("faction.general.already_in_faction")));
            return;

        }

        Faction attemptedFaction = null;

        if (factionManager.getFactionByPlayerName(args[0]) != null) {

            attemptedFaction = factionManager.getFactionByPlayerName(args[0]);

        } else if (factionManager.getFactionByName(args[0]) != null) {

            attemptedFaction = factionManager.getFactionByName(args[0]);

        } else {

            player.sendMessage(locale.translateString("faction.general.non_existent").replace("{faction}", args[0]));
            return;

        }

        if (!attemptedFaction.isOpen()) {
            if (!attemptedFaction.getInvitedPlayers().contains(player.getUniqueId())) {

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("faction.join.not_invited").replace("{faction}", attemptedFaction.getFactionName())));
                faction.sendOfficerMessage(ChatColor.translateAlternateColorCodes('&', locale.getString("faction.join.attempted_join")));
                return;

            }
        }

        if (faction.getFactionMembers().size() >= instance.getSettings().getInt("faction.max_players")) {

            player.sendMessage(locale.translateString("faction.general.max_players_reached"));
            return;

        }

        player.sendMessage(locale.translateString("faction.join.success").replace("{faction}", faction.getFactionName()));
        faction.sendMessage(locale.translateString("faction.join.success_broadcast").replace("{player}", player.getName()));

        faction.getFactionMembers().put(player.getUniqueId(), FactionRole.MEMBER);

    }

}
