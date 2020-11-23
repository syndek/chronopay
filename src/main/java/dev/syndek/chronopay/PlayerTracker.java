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

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerTracker {
    private final ChronoPayPlugin        plugin;
    private final Map<String, Set<UUID>> playersAtAddress = new HashMap<>();
    private final Map<UUID, PlayerData>  playerData       = new HashMap<>();
    private final Set<UUID>              afkPlayers       = new HashSet<>();
    private final Set<Player>            validPlayers     = ConcurrentHashMap.newKeySet();

    public PlayerTracker(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getOnlinePlayers().forEach(this::startTrackingPlayer);
    }

    public @NotNull Set<Player> getValidPlayers() {
        return this.validPlayers;
    }

    public @NotNull Map<UUID, PlayerData> getPlayerData() {
        return this.playerData;
    }

    public @NotNull PlayerData getPlayerData(final @NotNull UUID uniqueId) {
        return this.playerData.computeIfAbsent(uniqueId, key -> new PlayerData());
    }

    public void startTrackingPlayer(final @NotNull Player player) {
        // Add the player to the afkPlayers set if Essentials is found and they're AFK.
        // AFK players are tracked regardless of whether or not AFK checking is enabled in the config.
        // This is to prevent needing to recalculate who is and isn't AFK if the value is changed at runtime.
        final IEssentials essentials = this.plugin.getEssentials();
        if (essentials != null && essentials.getUser(player).isAfk()) {
            this.afkPlayers.add(player.getUniqueId());
        }

        if (this.playerFailsAfkCheck(player)) {
            this.plugin.trySendGoneAfkMessageToPlayer(player);
        }

        this.addPlayerAddress(player);

        if (this.playerFailsAddressCheck(player)) {
            this.plugin.trySendMultipleAccountsMessageToPlayer(player);
        }

        // this.recalculatePlayerValidity(player);
        // No need to call this here. It's handled by addPlayerAddress(Player) if necessary.
    }

    public void stopTrackingPlayer(final @NotNull Player player) {
        this.validPlayers.remove(player);
        this.afkPlayers.remove(player.getUniqueId());
        this.removePlayerAddress(player);
    }

    public void setPlayerAfkStatus(final @NotNull Player player, final boolean isAfk) {
        if (isAfk) {
            this.afkPlayers.add(player.getUniqueId());
        } else {
            this.afkPlayers.remove(player.getUniqueId());
        }

        this.recalculatePlayerValidity(player);
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
            this.getPlayerData(player.getUniqueId()).getPayoutCount() >= this.plugin.getSettings().getPayoutCap();
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

        this.handlePlayersAtSameAddress(playersAtAddress);
    }

    private void addPlayerAddress(final @NotNull Player player) {
        final UUID playerId = player.getUniqueId();
        final String playerAddress = player.getAddress().getHostString();
        final Set<UUID> playersAtAddress = this.playersAtAddress.computeIfAbsent(playerAddress, key -> new HashSet<>());

        playersAtAddress.add(playerId);

        this.handlePlayersAtSameAddress(playersAtAddress);
    }

    private void handlePlayersAtSameAddress(final @NotNull Set<UUID> playerIds) {
        for (final UUID playerIdAtAddress : playerIds) {
            final Player playerAtAddress = this.plugin.getServer().getPlayer(playerIdAtAddress);

            if (playerAtAddress == null) {
                continue;
            }

            this.recalculatePlayerValidity(playerAtAddress);
        }
    }
}