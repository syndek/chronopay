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

import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class PlayerAfkListener implements Listener {
    private final ChronoPayPlugin plugin;
    private final PlayerTracker   playerTracker;

    public PlayerAfkListener(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.playerTracker = plugin.getPlayerTracker();
    }

    @EventHandler
    private void onAfkStatusChange(final @NotNull AfkStatusChangeEvent event) {
        final Player player = event.getAffected().getBase();

        this.playerTracker.setPlayerAfkStatus(player, event.getValue());

        if (this.playerTracker.playerFailsAfkCheck(player)) {
            this.plugin.trySendGoneAfkMessageToPlayer(player);
        }
    }
}