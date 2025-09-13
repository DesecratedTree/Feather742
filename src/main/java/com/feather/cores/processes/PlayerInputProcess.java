package com.feather.cores.processes;

import com.feather.Settings;
import com.feather.game.World;
import com.feather.game.player.Player;
import com.feather.utils.Logger;
import com.feather.utils.Utils;

public class PlayerInputProcess extends BaseGameProcess {
    //private final PacketManager packetManager = PacketManager.getInstance();

    @Override
    protected void doProcess() {
        try {
            long currentTime = Utils.currentTimeMillis();

            // First, handle connection timeouts
            for (Player player : World.getPlayers()) {
                if (player == null || !player.hasStarted() || player.hasFinished())
                    continue;

                // Check for packet decoder timeout
                if (currentTime - player.getPacketsDecoderPing() > Settings.MAX_PACKETS_DECODER_PING_DELAY
                        && player.getSession().getChannel().isOpen()) {
                    player.getSession().getChannel().close();
                }
            }

            // Process all queued packets during this tick
            // This ensures packets are processed in the correct order within the game loop
            //packetManager.processPackets();

        } catch (Throwable e) {
            Logger.handle(e);
        }
    }
}