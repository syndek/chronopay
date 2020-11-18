/*
 * Copyright (C) 2020 Louis Salkeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.syndek.chronopay;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public final class PayoutCycleResetTask implements Runnable {
    public final ChronoPayPlugin plugin;

    public PayoutCycleResetTask(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final String cycleResetMessage = this.plugin.getSettings().getCycleResetMessage();

        for (final Map.Entry<UUID, PlayerData> entry : this.plugin.getPlayerTracker().getPlayerData().entrySet()) {
            final Player player = this.plugin.getServer().getPlayer(entry.getKey());

            if (player != null) {
                if (!cycleResetMessage.isEmpty() && this.plugin.getPlayerTracker().playerFailsCapCheck(player)) {
                    player.sendMessage(cycleResetMessage);
                }
            }

            // Payout count must be reset *after* the reset message is sent to ensure it's sent to the right players.
            entry.getValue().resetPayoutCount();

            if (player != null) {
                this.plugin.getPlayerTracker().recalculatePlayerValidity(player);
            }
        }
    }
}