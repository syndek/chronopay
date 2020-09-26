package dev.syndek.chronopay;

import org.bukkit.entity.Player;

import java.util.*;

public class PlayerTracker {
    private final ChronoPayPlugin        plugin;
    private final Map<String, Set<UUID>> playersAtAddress = new HashMap<>();
    private final Map<UUID, PlayerData>  playerData       = new HashMap<>();
    private final Set<UUID>              afkPlayers       = new HashSet<>();
    private final Set<Player>            validPlayers     = new HashSet<>();

    public PlayerTracker(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getOnlinePlayers().forEach(this::recalculatePlayerValidity);
    }

    public Set<Player> getValidPlayers() {
        return this.validPlayers;
    }

    public Map<UUID, PlayerData> getPlayerData() {
        return this.playerData;
    }

    public PlayerData getPlayerData(final UUID uniqueId) {
        return this.playerData.computeIfAbsent(uniqueId, key -> new PlayerData());
    }

    public void setPlayerAfkStatus(final Player player, final boolean isAfk) {
        if (isAfk) {
            this.afkPlayers.add(player.getUniqueId());
        } else {
            this.afkPlayers.remove(player.getUniqueId());
        }
    }

    public void recalculatePlayerValidity(final UUID playerId) {
        this.recalculatePlayerValidity(this.plugin.getServer().getPlayer(playerId));
    }

    public void recalculatePlayerValidity(final Player player) {
        if (this.playerFailsAddressCheck(player) ||
            this.playerFailsAfkCheck(player) ||
            this.playerFailsCapCheck(player)
        ) {
            this.validPlayers.remove(player);
        } else {
            this.validPlayers.add(player);
        }
    }

    public boolean playerFailsAddressCheck(final Player player) {
        if (this.plugin.getSettings().checkAddress() &&
            !player.hasPermission("chronopay.bypass.address")
        ) {
            final Set<UUID> playersAtAddress = this.playersAtAddress.get(player.getAddress().getHostString());
            return playersAtAddress != null && playersAtAddress.size() > 1;
        }

        return false;
    }

    public boolean playerFailsAfkCheck(final Player player) {
        return this.plugin.getSettings().checkAfk() &&
            !player.hasPermission("chronopay.bypass.afk") &&
            this.afkPlayers.contains(player.getUniqueId());
    }

    public boolean playerFailsCapCheck(final Player player) {
        return this.plugin.getSettings().checkCap() &&
            !player.hasPermission("chronopay.bypass.cap") &&
            this.getPlayerData(player.getUniqueId()).getPayedMoney() > this.plugin.getSettings().getPayoutCap();
    }

    public void addPlayerAddress(final Player player) {
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

    public void removePlayerAddress(final Player player) {
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