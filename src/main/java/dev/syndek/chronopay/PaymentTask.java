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

public final class PaymentTask implements Runnable {
    private final ChronoPayPlugin   plugin;
    private final ChronoPaySettings settings;
    private final PlayerTracker     playerTracker;

    public PaymentTask(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.playerTracker = plugin.getPlayerTracker();
    }

    @Override
    public void run() {
        for (final Player player : this.playerTracker.getValidPlayers()) {
            final PlayerData data = this.playerTracker.getPlayerData(player.getUniqueId());

            data.incrementOnlineTime();

            if (data.getOnlineTime() >= this.settings.getPayoutInterval()) {
                final float payoutAmount = this.settings.getPayoutAmount();

                // Pay the player the configured amount.
                this.plugin.getEconomy().depositPlayer(player, payoutAmount);

                data.addPayedMoney(payoutAmount);
                data.resetOnlineTime();

                final String payoutMessage = this.settings.getPayoutMessage();
                if (!payoutMessage.isEmpty()) {
                    player.sendMessage(payoutMessage);
                }

                // Recalculate player validity after payout to ensure they haven't exceeded the cap.
                this.playerTracker.recalculatePlayerValidity(player);

                if (this.playerTracker.playerFailsCapCheck(player)) {
                    final String capReachedMessage = this.settings.getCapReachedMessage();
                    if (!capReachedMessage.isEmpty()) {
                        player.sendMessage(capReachedMessage);
                    }
                }
            }
        }
    }
}