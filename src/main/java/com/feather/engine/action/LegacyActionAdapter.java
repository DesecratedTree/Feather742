package com.feather.engine.action;

import com.feather.game.player.Player;
import com.feather.game.player.actions.Action;

public class LegacyActionAdapter {

    private final Player player;
    private final Action action;
    private boolean started;
    private int internalDelay;

    public LegacyActionAdapter(Player player, Action action) {
        this.player = player;
        this.action = action;
    }

    public boolean init() {
        if (!action.start(player))
            return false;
        started = true;
        internalDelay = player.getActionManager().getActionDelay();
        return true;
    }

    public boolean tick() {
        if (!started)
            return false;

        if (!action.process(player))
            return false;

        if (internalDelay > 0) {
            internalDelay--;
            return true;
        }

        int delay = action.processWithDelay(player);
        if (delay == -1) {
            return false;
        }
        internalDelay += delay;
        return true;
    }

    public void stop() {
        if (started) {
            action.stop(player);
        }
    }

    public Action getAction() {
        return action;
    }
}
