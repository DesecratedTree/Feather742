package com.feather.engine;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.feather.engine.events.EventBus;
import com.feather.engine.events.GameLifecycle;
import com.feather.engine.processes.*;
import com.feather.plugin.PluginManager;
import com.feather.utils.Logger;

public final class GameEngine {

    static volatile boolean shutdown;
    public static WorldThread worldThread;
    public static ExecutorService serverWorkerChannelExecutor;
    public static ExecutorService serverBossChannelExecutor;
    public static Timer fastExecutor;
    public static ScheduledExecutorService slowExecutor;
    public static ScheduledExecutorService gameLoopExecutor;
    public static int serverWorkersCount;

    // Game processes (in correct order)
    private static EventBus eventBus;
    private static MapClock mapClock;
    private static WorldMainTickProcess worldMain;
    private static NpcPreTickProcess npcPreTick;
    private static PlayerIdShuffleProcess playerShuffle;
    private static PlayerInputProcess playerInput;
    private static PlayerRouteRequestProcess playerRouteRequest;
    private static NpcMainProcess npcMain;
    private static ControllerMainProcess controllerMain;
    private static PlayerMainProcess playerMain;
    private static PlayerLogoutProcess playerLogout;
    private static PlayerLoginProcess playerLogin;
    private static WorldPostTickProcess worldPostTick;
    private static PlayerPostTickProcess playerPostTick;

    // Game loop timing
    private static final int GAME_TICK_RATE = 600; // milliseconds per tick
    private static volatile boolean gameLoopRunning = false;
    private static volatile long currentTickNumber = 0;

    public static void init() {
        initializeExecutors();
        initializeProcesses();
        initializeLegacyComponents();
        startGameLoop();
        Logger.log("GameEngine", "Game Engine initialized and started.");
    }

    private static void initializeExecutors() {
        worldThread = new WorldThread();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        serverWorkersCount = availableProcessors >= 6 ? availableProcessors
                - (availableProcessors >= 12 ? 7 : 5) : 1;
        serverWorkerChannelExecutor = availableProcessors >= 6 ? Executors
                .newFixedThreadPool(availableProcessors
                                - (availableProcessors >= 12 ? 7 : 5),
                        new DecoderThreadFactory()) : Executors
                .newSingleThreadExecutor(new DecoderThreadFactory());
        serverBossChannelExecutor = Executors
                .newSingleThreadExecutor(new DecoderThreadFactory());
        fastExecutor = new Timer("Fast Executor");
        slowExecutor = availableProcessors >= 6 ? Executors
                .newScheduledThreadPool(availableProcessors >= 12 ? 4 : 2,
                        new SlowThreadFactory()) : Executors
                .newSingleThreadScheduledExecutor(new SlowThreadFactory());
        gameLoopExecutor = Executors.newSingleThreadScheduledExecutor(new GameLoopThreadFactory());
    }

    private static void initializeProcesses() {
        // Initialize all game processes
        eventBus = new EventBus();
        mapClock = new MapClock();
        worldMain = new WorldMainTickProcess();
        npcPreTick = new NpcPreTickProcess();
        playerShuffle = new PlayerIdShuffleProcess();
        playerInput = new PlayerInputProcess();
        playerRouteRequest = new PlayerRouteRequestProcess();
        npcMain = new NpcMainProcess();
        controllerMain = new ControllerMainProcess();
        playerMain = new PlayerMainProcess();
        playerLogout = new PlayerLogoutProcess();
        playerLogin = new PlayerLoginProcess();
        worldPostTick = new WorldPostTickProcess();
        playerPostTick = new PlayerPostTickProcess();

        Logger.log("GameEngine", "Game processes initialized.");
    }

    private static void initializeLegacyComponents() {
        worldThread.start();
        PluginManager.getInstance().loadPlugins();
        Logger.log("GameEngine", "Legacy components initialized.");
    }

    /**
     * Main game cycle - implements correct order of operations
     */
    public static void tick() {

        final boolean TICKDEBUG = false;
        if (shutdown) return;
        currentTickNumber++;

        if (TICKDEBUG) Logger.log("GameEngine", "Starting tick #" + currentTickNumber);

        try {
            // Start of cycle
            eventBus.publish(GameLifecycle.START_CYCLE);

            // Pre-tick phase
            preTick();

            // Main tick (map clock)
            mapClock.tick();

            // Late cycle event
            eventBus.publish(GameLifecycle.LATE_CYCLE);

            // Post-tick phase
            postTick();

            // End of cycle
            eventBus.publish(GameLifecycle.END_CYCLE);

            if (TICKDEBUG) Logger.log("GameEngine", "Completed tick #" + currentTickNumber);

        } catch (Exception e) {
            Logger.log("GameEngine", "Error during game tick #" + currentTickNumber + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Pre-tick phase - processes game state before map clock update
     */
    private static void preTick() {
        worldMain.process();
        npcPreTick.process();
        playerShuffle.process();
        playerInput.process();
        playerRouteRequest.process();
        npcMain.process();
        controllerMain.process();
        playerMain.process();
        playerLogout.process();
        playerLogin.process();
    }

    /**
     * Post-tick phase - processes that occur after map clock cycles
     * Note: These processes occur _after_ the map clock cycles, meaning
     * timing operations like loc_del(100) will trigger after 99 cycles, not 100.
     */
    private static void postTick() {
        worldPostTick.process();
        playerPostTick.process();
    }

    /**
     * Start the main game loop
     */
    public static void startGameLoop() {
        if (!gameLoopRunning) {
            gameLoopRunning = true;

            // Use Settings.WORLD_CYCLE_TIME if available, otherwise default to 600ms
            int cycleTime = GAME_TICK_RATE;
            try {
                cycleTime = com.feather.Settings.WORLD_CYCLE_TIME;
            } catch (Exception e) {
                Logger.log("GameEngine", "Could not access Settings.WORLD_CYCLE_TIME, using default 600ms");
            }

            gameLoopExecutor.scheduleAtFixedRate(
                    GameEngine::tick,
                    0,
                    cycleTime,
                    TimeUnit.MILLISECONDS
            );
            Logger.log("GameEngine", "Game loop started at " + cycleTime + "ms intervals.");
        }
    }

    /**
     * Stop the main game loop
     */
    public static void stopGameLoop() {
        gameLoopRunning = false;
        Logger.log("GameEngine", "Game loop stopped.");
    }

    public static void shutdown() {
        stopGameLoop();

        serverWorkerChannelExecutor.shutdown();
        serverBossChannelExecutor.shutdown();
        fastExecutor.cancel();
        slowExecutor.shutdown();

        if (gameLoopExecutor != null) {
            gameLoopExecutor.shutdown();
            try {
                if (!gameLoopExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    gameLoopExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                gameLoopExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        Logger.log("GameEngine", "Game Engine has been shutdown.");
        shutdown = true;
    }

    // Getters for game processes and state
    public static EventBus getEventBus() {
        return eventBus;
    }

    public static MapClock getMapClock() {
        return mapClock;
    }

    public static boolean isGameLoopRunning() {
        return gameLoopRunning;
    }

    public static long getCurrentTickNumber() {
        return currentTickNumber;
    }

    public static boolean isShutdown() {
        return shutdown;
    }

    // Individual process getters (for external access if needed)
    public static WorldMainTickProcess getWorldMain() {
        return worldMain;
    }

    public static NpcPreTickProcess getNpcPreTick() {
        return npcPreTick;
    }

    public static PlayerIdShuffleProcess getPlayerShuffle() {
        return playerShuffle;
    }

    public static PlayerInputProcess getPlayerInput() {
        return playerInput;
    }

    public static PlayerRouteRequestProcess getPlayerRouteRequest() {
        return playerRouteRequest;
    }

    public static NpcMainProcess getNpcMain() {
        return npcMain;
    }

    public static ControllerMainProcess getControllerMain() {
        return controllerMain;
    }

    public static PlayerMainProcess getPlayerMain() {
        return playerMain;
    }

    public static PlayerLogoutProcess getPlayerLogout() {
        return playerLogout;
    }

    public static PlayerLoginProcess getPlayerLogin() {
        return playerLogin;
    }

    public static WorldPostTickProcess getWorldPostTick() {
        return worldPostTick;
    }

    public static PlayerPostTickProcess getPlayerPostTick() {
        return playerPostTick;
    }

    private GameEngine() {
        // Private constructor to prevent instantiation
    }
}