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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public final class PlayerConnectionListener implements Listener {
    private final ChronoPayPlugin plugin;

    public PlayerConnectionListener(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(final @NotNull PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getPlayerTracker().addPlayerAddress(player);

        if (this.plugin.getPlayerTracker().playerFailsAddressCheck(player)) {
            final String multipleAccountsMessage = this.plugin.getSettings().getMultipleAccountsMessage();
            if (!multipleAccountsMessage.isEmpty()) {
                player.sendMessage(multipleAccountsMessage);
            }
        }
    }

    @EventHandler
    private void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        this.plugin.getPlayerTracker().removePlayerAddress(event.getPlayer());
    }
}