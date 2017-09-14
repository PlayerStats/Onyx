package support.plugin.onyx.factions;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import support.plugin.onyx.Onyx;
import support.plugin.onyx.factions.claim.Claim;
import support.plugin.onyx.factions.enums.FactionRole;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
 * An object to store faction data
 */
public class Faction {

    @Getter
    @Setter
    private UUID factionId;

    @Getter
    @Setter
    private String factionName;

    @Getter
    @Setter
    private UUID factionOwner;

    @Getter
    @Setter
    private Location factionHome;

    @Getter
    @Setter
    private HashMap<UUID, FactionRole> factionMembers;

    @Getter
    @Setter
    private Set<Claim> factionClaims;

    @Getter
    @Setter
    private double balance;

    @Getter
    @Setter
    private int lives;

    @Getter
    @Setter
    private double dtr;

    @Getter
    private Set<Faction> allies;

    @Getter
    private Set<Faction> invitedAllies;

    @Getter
    private Set<UUID> invitedPlayers;

    @Getter
    @Setter
    private int[] freezeTime;

    @Getter
    @Setter
    private boolean systemFaction;

    @Getter
    @Setter
    private boolean open;

    public Faction(UUID factionOwner, String factionName) {

        this.factionOwner = factionOwner;

        factionMembers = new HashMap<>();
        factionClaims = new HashSet<>();
        allies = new HashSet<>();
        invitedAllies = new HashSet<>();
        invitedPlayers = new HashSet<>();
        factionMembers.put(factionOwner, FactionRole.OWNER);

        factionHome = null;
        balance = 0;
        lives = 0;
        dtr = Onyx.getInstance().getSettings().getDouble("dtr.starting");
        open = false;
        systemFaction = false;

    }

    /**
     * Gets the maximum amount of DTR for a faction
     *
     * @return the maximum dtr
     */
    public double getMaxDtr() {

        if (systemFaction) {
            return 0.0;
        }

        return Onyx.getInstance().getSettings().getDouble("dtr.starting") + (Onyx.getInstance().getSettings().getDouble("dtr.per_player") * factionMembers.size());
    }

    /**
     * Gets all online members
     * @return the online members
     */
    public Set<Player> getOnlinePlayers() {

        Set<Player> online = new HashSet<>();

        for (UUID uuid : factionMembers.keySet()) {

            if (Bukkit.getPlayer(uuid) != null) {
                online.add(Bukkit.getPlayer(uuid));
            }

        }

        return online;

    }

    /**
     * Gets all online officers
     * @return the online officers
     */
    public Set<Player> getOnlineOfficers() {

        Set<Player> online = new HashSet<>();

        for (UUID uuid : factionMembers.keySet()) {

            if (Bukkit.getPlayer(uuid) != null) {
                if (factionMembers.get(uuid).getRank() >= 2) {
                    online.add(Bukkit.getPlayer(uuid));
                }
            }

        }

        return online;

    }

    /**
     * Gets all online allies
     * @return the online allies
     */
    public Set<Player> getOnlineAllies() {

        Set<Player> players = new HashSet<>();

        for (Faction ally : allies) {

            for (Player allied : ally.getOnlinePlayers()) {
                players.add(allied);
            }

        }

        return players;

    }

    /**
     * Checks if a player is a member
     * @param uuid the player's uuid
     * @return whether the player is in the faction or not
     */
    public boolean contains(UUID uuid) {

        return factionMembers.containsKey(uuid);

    }

    /**
     * Gets the role of a player
     * @param uuid the player's uuid
     * @return the role of the member
     */
    public FactionRole getRole(UUID uuid) {

        return factionMembers.get(uuid);

    }

    /**
     * Checks if a faction is allied with this faction
     * @param faction the faction in question
     * @return whether both factions are allied or not
     */
    public boolean isAllied(Faction faction) {

        return allies.contains(faction);

    }

    /**
     * Checks if the faction is on DTR freeze
     * @return whether the faction is frozen or not
     */
    public boolean isFrozen() {

        return freezeTime != null;

    }

    /**
     * Checks if the faction is frozen
     * @param duration the duration for the freeze time
     */
    public void freeze(int duration) {

        freezeTime = new int[]{duration, (int) (System.currentTimeMillis() / 1000)};

    }

    /**
     * Sends a message to all online members
     * @param message the message
     */
    public void sendMessage(String message) {

        for (Player player : getOnlinePlayers()) {

            player.sendMessage(message);

        }

    }

    /**
     * Sends a message to all online officers
     * @param message the message
     */
    public void sendOfficerMessage(String message) {

        for (Player player : getOnlineOfficers()) {

            player.sendMessage(message);

        }

    }

    @Deprecated // Now moved to FactionManager.java which iterates over each faction for more efficiency
    private synchronized void runTasks() {

        if (systemFaction) {
            return;
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Onyx.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (isFrozen()) {
                    if (System.currentTimeMillis() / 1000 - freezeTime[1] >= freezeTime[0]) {
                        setFreezeTime(null);
                    }
                }
            }
        }, 20L, 20L);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Onyx.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (isFrozen()) {
                    if (!(isFrozen()) && dtr < getMaxDtr()) {
                        setDtr(getDtr() + Onyx.getInstance().getSettings().getInt("dtr.regeneration.addition"));
                    }
                }
            }
        }, Onyx.getInstance().getSettings().getInt("dtr.regeneration.interval") * 20L, Onyx.getInstance().getSettings().getInt("dtr.regeneration.interval") * 20L);
    }

}
