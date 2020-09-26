package dev.syndek.chronopay;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public final class PayoutCycleResetTask implements Runnable {
    public final ChronoPayPlugin plugin;

    public PayoutCycleResetTask(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final String cycleResetMessage = this.plugin.getSettings().getCycleResetMessage();

        for (final Map.Entry<UUID, PlayerData> entry : this.plugin.getPlayerTracker().getPlayerData().entrySet()) {
            entry.getValue().resetPayedMoney();

            final Player player = this.plugin.getServer().getPlayer(entry.getKey());

            if (player != null) {
                if (!cycleResetMessage.isEmpty() && this.plugin.getPlayerTracker().playerFailsCapCheck(player)) {
                    player.sendMessage(cycleResetMessage);
                }

                this.plugin.getPlayerTracker().recalculatePlayerValidity(player);
            }
        }
    }
}