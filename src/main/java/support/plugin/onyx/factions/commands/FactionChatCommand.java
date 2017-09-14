package support.plugin.onyx.factions.commands;

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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import support.plugin.onyx.Onyx;
import support.plugin.onyx.commands.handler.ICommand;
import support.plugin.onyx.commands.handler.Info;
import support.plugin.onyx.profiles.GameProfile;
import support.plugin.onyx.profiles.dto.ChatMode;

/**
 * Handles the sub-command for toggling faction chat
 */
@Info(subCommand = "chat", description = "Toggle your chat channel", usage = "[type]", permission = "onyx.factions.chat", aliases = {"c"})
public class FactionChatCommand implements ICommand {

    private Onyx instance;

    public FactionChatCommand(Onyx instance) {
        this.instance = instance;
    }

    @Override
    public void execute(Player player, String[] args) {

        GameProfile profile = instance.getProfileManager().getUser(player.getUniqueId());

        if (args.length == 0) {

            toggleChat(profile);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have toggled your chat channel to &l" + profile.getChatMode().name()));

        } else {

            profile.setChatMode(findChatMode(args[0]));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have toggled your chat channel to &l" + profile.getChatMode().name()));

        }

    }

    private ChatMode findChatMode(String strMode) {

        if (strMode.equalsIgnoreCase("public") || strMode.equalsIgnoreCase("pub") || strMode.equalsIgnoreCase("p")) {

            return ChatMode.PUBLIC;

        } else if (strMode.equalsIgnoreCase("ally") || strMode.equalsIgnoreCase("a")) {

            return ChatMode.ALLY;

        } else if (strMode.equalsIgnoreCase("faction") || strMode.equalsIgnoreCase("fac") || strMode.equalsIgnoreCase("f")) {

            return ChatMode.FACTION;

        } else if (strMode.equalsIgnoreCase("officer") || strMode.equalsIgnoreCase("o")) {

            return ChatMode.OFFICER;

        } else {

            return ChatMode.PUBLIC;

        }

    }

    private void toggleChat(GameProfile profile) {

        switch (profile.getChatMode()) {

            case PUBLIC:
                profile.setChatMode(ChatMode.ALLY);
            case ALLY:
                profile.setChatMode(ChatMode.FACTION);
            case FACTION:
                profile.setChatMode(ChatMode.OFFICER);
            case OFFICER:
                profile.setChatMode(ChatMode.PUBLIC);

        }

    }

}
