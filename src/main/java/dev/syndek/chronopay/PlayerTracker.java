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

import java.util.*;

public class PlayerTracker {
    private final ChronoPayPlugin        plugin;
    private final Map<String, Set<UUID>> playersAtAddress = new HashMap<>();
    private final Map<UUID, PlayerData>  playerData       = new HashMap<>();
    private final Set<UUID>              afkPlayers       = new HashSet<>();
    private final Set<Player>            validPlayers     = new HashSet<>();

    public PlayerTracker(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getOnlinePlayers().forEach(this::recalculatePlayerValidity);
    }

    public @NotNull Set<Player> getValidPlayers() {
        return this.validPlayers;
    }

    public @NotNull Map<UUID, PlayerData> getPlayerData() {
        return this.playerData;
    }

    public @NotNull PlayerData getPlayerData(final UUID uniqueId) {
        return this.playerData.computeIfAbsent(uniqueId, key -> new PlayerData());
    }

    public void setPlayerAfkStatus(final @NotNull Player player, final boolean isAfk) {
        if (isAfk) {
            this.afkPlayers.add(player.getUniqueId());
        } else {
            this.afkPlayers.remove(player.getUniqueId());
        }
    }

    public void recalculatePlayerValidity(final @NotNull UUID playerId) {
        this.recalculatePlayerValidity(this.plugin.getServer().getPlayer(playerId));
    }

    public void recalculatePlayerValidity(final @NotNull Player player) {
        if (this.playerFailsAddressCheck(player) ||
            this.playerFailsAfkCheck(player) ||
            this.playerFailsCapCheck(player)
        ) {
            this.validPlayers.remove(player);
        } else {
            this.validPlayers.add(player);
        }
    }

    public boolean playerFailsAddressCheck(final @NotNull Player player) {
        if (this.plugin.getSettings().checkAddress() &&
            !player.hasPermission("chronopay.bypass.address")
        ) {
            final Set<UUID> playersAtAddress = this.playersAtAddress.get(player.getAddress().getHostString());
            return playersAtAddress != null && playersAtAddress.size() > 1;
        }

        return false;
    }

    public boolean playerFailsAfkCheck(final @NotNull Player player) {
        return this.plugin.getSettings().checkAfk() &&
            !player.hasPermission("chronopay.bypass.afk") &&
            this.afkPlayers.contains(player.getUniqueId());
    }

    public boolean playerFailsCapCheck(final @NotNull Player player) {
        return this.plugin.getSettings().checkCap() &&
            !player.hasPermission("chronopay.bypass.cap") &&
            this.getPlayerData(player.getUniqueId()).getPayedMoney() >= this.plugin.getSettings().getPayoutCap();
    }

    public void addPlayerAddress(final @NotNull Player player) {
        final UUID playerId = player.getUniqueId();
        final String playerAddress = player.getAddress().getHostString();
        final Set<UUID> playersAtAddress = this.playersAtAddress.computeIfAbsent(playerAddress, key -> new HashSet<>());

        playersAtAddress.add(playerId);

        // Recalculate the validity of all players at the address.
        for (final UUID playerIdAtAddress : playersAtAddress) {
            // If the player being tested is the same that's being passed to this method,
            // we can use the faster recalculatePlayerValidity(Player) method.
            if (playerIdAtAddress.equals(playerId)) {
                this.recalculatePlayerValidity(player);
            } else {
                this.recalculatePlayerValidity(playerIdAtAddress);
            }
        }
    }

    public void removePlayerAddress(final @NotNull Player player) {
        final UUID playerId = player.getUniqueId();
        final String playerAddress = player.getAddress().getHostString();
        final Set<UUID> playersAtAddress = this.playersAtAddress.get(playerAddress);

        // No playersAtAddress value means no other players are logged in from the same address.
        if (playersAtAddress == null) {
            return;
        }

        playersAtAddress.remove(playerId);

        // Recalculate the validity of all players at the same address.
        for (final UUID playerIdAtAddress : playersAtAddress) {
            this.recalculatePlayerValidity(playerIdAtAddress);
        }
    }
}