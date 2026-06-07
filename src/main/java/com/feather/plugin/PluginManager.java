package com.feather.plugin;

import com.feather.plugin.handler.*;
import com.feather.utils.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PluginManager {

    private static final PluginManager INSTANCE = new PluginManager();

    private final Map<String, Plugin> plugins = new LinkedHashMap<>();
    private final Map<Integer, Map<Integer, List<PluginNPCInteractionListener>>> npcListeners = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, List<PluginObjectInteractionListener>>> objectListeners = new ConcurrentHashMap<>();
    private final Map<Integer, List<PluginPlayerInteractionListener>> playerListeners = new ConcurrentHashMap<>();

    private boolean loaded = false;

    private PluginManager() {}

    public static PluginManager getInstance() {
        return INSTANCE;
    }

    public void loadPlugins() {
        if (loaded) {
            Logger.log("PluginManager", "Plugins already loaded.");
            return;
        }

        Logger.log("PluginManager", "Scanning for plugins...");

        try {
            Reflections reflections = new Reflections("com.feather.plugin");
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(PluginMeta.class);

            for (Class<?> clazz : annotated) {
                if (!Plugin.class.isAssignableFrom(clazz)) {
                    Logger.log("PluginManager", "Skipping " + clazz.getName() + ": does not implement Plugin interface.");
                    continue;
                }

                PluginMeta meta = clazz.getAnnotation(PluginMeta.class);
                try {
                    Plugin plugin = (Plugin) clazz.getDeclaredConstructor().newInstance();
                    plugin.init();
                    plugins.put(meta.name(), plugin);
                    Logger.log("PluginManager", "Loaded plugin: " + meta.name() + " v" + meta.version());
                } catch (Exception e) {
                    Logger.log("PluginManager", "Failed to instantiate plugin " + clazz.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            scanNPCListeners(annotated);
            scanObjectListeners(annotated);
            scanPlayerListeners(annotated);

            for (Plugin plugin : plugins.values()) {
                plugin.start();
            }

            loaded = true;
            Logger.log("PluginManager", "Loaded " + plugins.size() + " plugins.");
        } catch (Exception e) {
            Logger.log("PluginManager", "Failed to scan plugins: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void scanNPCListeners(Set<Class<?>> annotated) {
        for (Class<?> clazz : annotated) {
            for (Method method : clazz.getDeclaredMethods()) {
                com.feather.plugin.handler.NPCHandler handler = method.getAnnotation(com.feather.plugin.handler.NPCHandler.class);
                if (handler == null) continue;

                int op = handler.op();
                int[] npcIds = handler.npcIds();
                method.setAccessible(true);

                PluginNPCInteractionListener listener = (player, npc, opNum) -> {
                    try {
                        if (op == -1 || op == opNum) {
                            method.invoke(
                                clazz.getDeclaredConstructor().newInstance(),
                                player, npc, opNum
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                for (int npcId : npcIds) {
                    npcListeners
                        .computeIfAbsent(npcId, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(op, k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(listener);
                }
            }
        }
    }

    private void scanObjectListeners(Set<Class<?>> annotated) {
        for (Class<?> clazz : annotated) {
            for (Method method : clazz.getDeclaredMethods()) {
                com.feather.plugin.handler.ObjectHandler handler = method.getAnnotation(com.feather.plugin.handler.ObjectHandler.class);
                if (handler == null) continue;

                int op = handler.op();
                int[] objectIds = handler.objectIds();
                method.setAccessible(true);

                PluginObjectInteractionListener listener = (player, object, opNum) -> {
                    try {
                        if (op == -1 || op == opNum) {
                            method.invoke(
                                clazz.getDeclaredConstructor().newInstance(),
                                player, object, opNum
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                for (int objectId : objectIds) {
                    objectListeners
                        .computeIfAbsent(objectId, k -> new ConcurrentHashMap<>())
                        .computeIfAbsent(op, k -> Collections.synchronizedList(new ArrayList<>()))
                        .add(listener);
                }
            }
        }
    }

    private void scanPlayerListeners(Set<Class<?>> annotated) {
        for (Class<?> clazz : annotated) {
            for (Method method : clazz.getDeclaredMethods()) {
                com.feather.plugin.handler.PlayerHandler handler = method.getAnnotation(com.feather.plugin.handler.PlayerHandler.class);
                if (handler == null) continue;

                int op = handler.op();
                method.setAccessible(true);

                PluginPlayerInteractionListener listener = (player, target, opNum) -> {
                    try {
                        if (op == -1 || op == opNum) {
                            method.invoke(
                                clazz.getDeclaredConstructor().newInstance(),
                                player, target, opNum
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };

                playerListeners
                    .computeIfAbsent(op, k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(listener);
            }
        }
    }

    public List<PluginNPCInteractionListener> getNPCListeners(int npcId, int opNum) {
        List<PluginNPCInteractionListener> result = new ArrayList<>();
        Map<Integer, List<PluginNPCInteractionListener>> byOp = npcListeners.get(npcId);
        if (byOp == null) return result;
        List<PluginNPCInteractionListener> exact = byOp.get(opNum);
        if (exact != null) result.addAll(exact);
        List<PluginNPCInteractionListener> wildcard = byOp.get(-1);
        if (wildcard != null) result.addAll(wildcard);
        return result;
    }

    public boolean hasNPCListeners(int npcId, int opNum) {
        Map<Integer, List<PluginNPCInteractionListener>> byOp = npcListeners.get(npcId);
        if (byOp == null) return false;
        return byOp.containsKey(opNum) || byOp.containsKey(-1);
    }

    public List<PluginObjectInteractionListener> getObjectListeners(int objectId, int opNum) {
        List<PluginObjectInteractionListener> result = new ArrayList<>();
        Map<Integer, List<PluginObjectInteractionListener>> byOp = objectListeners.get(objectId);
        if (byOp == null) return result;
        List<PluginObjectInteractionListener> exact = byOp.get(opNum);
        if (exact != null) result.addAll(exact);
        List<PluginObjectInteractionListener> wildcard = byOp.get(-1);
        if (wildcard != null) result.addAll(wildcard);
        return result;
    }

    public boolean hasObjectListeners(int objectId, int opNum) {
        Map<Integer, List<PluginObjectInteractionListener>> byOp = objectListeners.get(objectId);
        if (byOp == null) return false;
        return byOp.containsKey(opNum) || byOp.containsKey(-1);
    }

    public List<PluginPlayerInteractionListener> getPlayerListeners(int opNum) {
        List<PluginPlayerInteractionListener> result = new ArrayList<>();
        List<PluginPlayerInteractionListener> exact = playerListeners.get(opNum);
        if (exact != null) result.addAll(exact);
        List<PluginPlayerInteractionListener> wildcard = playerListeners.get(-1);
        if (wildcard != null) result.addAll(wildcard);
        return result;
    }

    public boolean hasPlayerListeners(int opNum) {
        return playerListeners.containsKey(opNum) || playerListeners.containsKey(-1);
    }

    public void unload() {
        for (Plugin plugin : plugins.values()) {
            try {
                plugin.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plugins.clear();
        npcListeners.clear();
        objectListeners.clear();
        playerListeners.clear();
        loaded = false;
        Logger.log("PluginManager", "All plugins unloaded.");
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Map<String, Plugin> getPlugins() {
        return Collections.unmodifiableMap(plugins);
    }
}
