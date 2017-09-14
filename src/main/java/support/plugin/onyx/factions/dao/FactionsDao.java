package support.plugin.onyx.factions.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import support.plugin.onyx.Onyx;
import support.plugin.onyx.factions.Faction;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
 * Stands for 'Data Access Object', saves and loads data to/from Redis.
 */
public class FactionsDao {

    private final Gson gson;
    private final Jedis jedis;

    public FactionsDao(String hostname, String port, String auth) {

        gson = new GsonBuilder().setPrettyPrinting().create();
        jedis = new Jedis(hostname, Integer.parseInt(port), 3000);

        if (!(auth == null)) {
            jedis.auth(auth);
        }

    }

    /**
     * Inserts a record into the keystore
     *
     * @param faction the faction in question
     */
    public void insert(Faction faction) {

        try (Jedis conn = jedis) {

            conn.set(getKey(faction), gson.toJson(faction));

        }

    }

    /**
     * Updates a record inside the keystore
     * @param faction the faction in question
     */
    public void update(Faction faction) {

        try (Jedis conn = jedis) {

            conn.set(getKey(faction), gson.toJson(faction));

        }

    }

    /**
     * Removes a record from the keystore
     * @param faction the faction in question
     */
    public void delete(Faction faction) {

        try (Jedis conn = jedis) {

            conn.del(getKey(faction));

        }

    }

    /**
     * Returns all records inside the keystore
     * @return the list of factions
     */
    public List<Faction> getAll() {

        try (Jedis conn = jedis) {

            return conn.keys("onyx:factions:" + Onyx.getInstance().getSettings().getInt("map.identifier") + ":*").stream().map(k -> gson.fromJson(conn.get(k), Faction.class)).collect(Collectors.toList());

        }

    }

    /**
     * Saves all records to the keystore
     * @param factions the factions you want to save
     */
    public void saveAll(List<Faction> factions) {

        for (Faction faction : factions) {

            update(faction);

        }

    }

    /**
     * Gets a faction from the keystore
     * @param uuid the faction uuid
     * @return the faction
     */
    public Faction getFaction(UUID uuid) {

        for (Faction faction : getAll()) {

            if (faction.getFactionId() == uuid) {
                return faction;
            }

        }

        return null;

    }

    /**
     * Ensures that all records are created on the same key 'baseline'..?
     * @param faction the faction in question
     * @return a key for the profile in question
     */
    public String getKey(Faction faction) {
        return "onyx:factions:" + Onyx.getInstance().getSettings().getInt("map.identifier") + ":" + faction.getFactionId().toString();
    }

}
