package com.feather.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.feather.cache.parser.NPCDefinitions;
import com.feather.game.World;
import com.feather.game.WorldTile;
import com.feather.game.npc.NPC;

public final class NPCSpawns {

    private static final Object lock = new Object();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean addSpawn(String username, int id, WorldTile tile) throws Throwable {
        synchronized(lock) {
            File file = new File("data/npcs/spawns.json");
            JsonArray spawnsArray;

            // Read existing JSON file or create new array
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                JsonElement element = JsonParser.parseReader(reader);
                reader.close();
                spawnsArray = element.getAsJsonArray();
            } else {
                spawnsArray = new JsonArray();
            }

            // Create new spawn entry
            JsonObject spawnEntry = new JsonObject();
            spawnEntry.addProperty("comment", NPCDefinitions.getNPCDefinitions(id).name + ", " +
                    NPCDefinitions.getNPCDefinitions(id).combatLevel + ", added by: " + username);
            spawnEntry.addProperty("npcId", id);

            JsonObject tileObject = new JsonObject();
            tileObject.addProperty("x", tile.getX());
            tileObject.addProperty("y", tile.getY());
            tileObject.addProperty("plane", tile.getPlane());
            spawnEntry.add("tile", tileObject);

            spawnsArray.add(spawnEntry);

            // Write updated JSON back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(gson.toJson(spawnsArray));
            writer.close();

            World.spawnNPC(id, tile, -1, true);
            return true;
        }
    }

    public static boolean removeSpawn(NPC npc) throws Throwable {
        synchronized(lock) {
            File file = new File("data/npcs/spawns.json");
            if (!file.exists()) {
                return false;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            JsonElement element = JsonParser.parseReader(reader);
            reader.close();
            JsonArray spawnsArray = element.getAsJsonArray();

            boolean removed = false;
            int id = npc.getId();
            WorldTile tile = npc.getRespawnTile();

            // Find and remove matching spawn
            for (int i = 0; i < spawnsArray.size(); i++) {
                JsonObject spawn = spawnsArray.get(i).getAsJsonObject();
                if (spawn.get("npcId").getAsInt() == id) {
                    JsonObject tileObj = spawn.get("tile").getAsJsonObject();
                    if (tileObj.get("x").getAsInt() == tile.getX() &&
                            tileObj.get("y").getAsInt() == tile.getY() &&
                            tileObj.get("plane").getAsInt() == tile.getPlane()) {
                        spawnsArray.remove(i);
                        removed = true;
                        break;
                    }
                }
            }

            if (!removed) {
                return false;
            }

            // Write updated JSON back to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(gson.toJson(spawnsArray));
            writer.close();

            npc.finish();
            return true;
        }
    }

    public static final void init() {
        if (!new File("data/npcs/packedSpawns").exists())
            packNPCSpawns();
    }

    private static final void packNPCSpawns() {
        Logger.log("NPCSpawns", "Packing npc spawns from JSON...");
        if (!new File("data/npcs/packedSpawns").mkdir())
            throw new RuntimeException(
                    "Couldn't create packedSpawns directory.");
        try {
            File jsonFile = new File("data/npcs/spawns.json");
            if (!jsonFile.exists()) {
                Logger.log("NPCSpawns", "No spawns.json file found, skipping packing.");
                return;
            }

            BufferedReader reader = new BufferedReader(new FileReader(jsonFile));
            JsonElement element = JsonParser.parseReader(reader);
            reader.close();
            JsonArray spawnsArray = element.getAsJsonArray();

            for (JsonElement spawnElement : spawnsArray) {
                JsonObject spawn = spawnElement.getAsJsonObject();
                int npcId = spawn.get("npcId").getAsInt();
                JsonObject tileObj = spawn.get("tile").getAsJsonObject();

                WorldTile tile = new WorldTile(
                        tileObj.get("x").getAsInt(),
                        tileObj.get("y").getAsInt(),
                        tileObj.get("plane").getAsInt());

                int mapAreaNameHash = -1;
                boolean canBeAttackFromOutOfArea = true;

                // Check for optional fields
                if (spawn.has("mapAreaName")) {
                    mapAreaNameHash = Utils.getNameHash(spawn.get("mapAreaName").getAsString());
                }
                if (spawn.has("canBeAttackFromOutOfArea")) {
                    canBeAttackFromOutOfArea = spawn.get("canBeAttackFromOutOfArea").getAsBoolean();
                }

                addNPCSpawn(npcId, tile.getRegionId(), tile, mapAreaNameHash,
                        canBeAttackFromOutOfArea);
            }
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }

    public static final void loadNPCSpawns(int regionId) {
        File file = new File("data/npcs/packedSpawns/" + regionId + ".ns");
        if (!file.exists())
            return;
        try {
            RandomAccessFile in = new RandomAccessFile(file, "r");
            FileChannel channel = in.getChannel();
            ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
                    channel.size());
            while (buffer.hasRemaining()) {
                int npcId = buffer.getShort() & 0xffff;
                int plane = buffer.get() & 0xff;
                int x = buffer.getShort() & 0xffff;
                int y = buffer.getShort() & 0xffff;
                boolean hashExtraInformation = buffer.get() == 1;
                int mapAreaNameHash = -1;
                boolean canBeAttackFromOutOfArea = true;
                if (hashExtraInformation) {
                    mapAreaNameHash = buffer.getInt();
                    canBeAttackFromOutOfArea = buffer.get() == 1;
                }
                World.spawnNPC(npcId, new WorldTile(x, y, plane),
                        mapAreaNameHash, canBeAttackFromOutOfArea);
            }
            channel.close();
            in.close();
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }

    private static final void addNPCSpawn(int npcId, int regionId,
                                          WorldTile tile, int mapAreaNameHash,
                                          boolean canBeAttackFromOutOfArea) {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(
                    "data/npcs/packedSpawns/" + regionId + ".ns", true));
            out.writeShort(npcId);
            out.writeByte(tile.getPlane());
            out.writeShort(tile.getX());
            out.writeShort(tile.getY());
            out.writeBoolean(mapAreaNameHash != -1);
            if (mapAreaNameHash != -1) {
                out.writeInt(mapAreaNameHash);
                out.writeBoolean(canBeAttackFromOutOfArea);
            }
            out.flush();
            out.close();
        } catch (Throwable e) {
            Logger.handle(e);
        }
    }

    private NPCSpawns() {
    }
}