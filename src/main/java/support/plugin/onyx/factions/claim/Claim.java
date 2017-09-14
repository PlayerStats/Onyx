package support.plugin.onyx.factions.claim;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import support.plugin.onyx.factions.Faction;
import support.plugin.onyx.utils.LocationUtils;

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
 * An object for claiming
 */
public class Claim {

    @Getter
    @Setter
    private Location cornerA, cornerB;

    @Getter
    @Setter
    private Faction owner;

    @Getter
    @Setter
    private boolean deathban;

    @Getter
    @Setter
    private double price;

    /**
     * @param location the location in question
     * @return whether the location is inside the claim or not
     */
    public boolean insideClaim(Location location) {

        return LocationUtils.betweenPoints(location, cornerA, cornerB);

    }

    /**
     *
     * @param player the player in question
     * @return whether the player is inside the claim or not
     */
    public boolean insideClaim(Player player) {

        return LocationUtils.betweenPoints(player.getLocation(), cornerA, cornerB);

    }

}
