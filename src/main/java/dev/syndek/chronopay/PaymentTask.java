package dev.syndek.chronopay;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public final class PaymentTask implements Runnable {
    private final ChronoPayPlugin    plugin;
    private final ChronoPaySettings  settings;
    private final Map<UUID, Float>   payedMoneyMap;
    private final Map<UUID, String>  onlineAddressMap;
    private final Map<UUID, Integer> onlineTimeMap;

    public PaymentTask(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.payedMoneyMap = plugin.getPayedMoneyMap();
        this.onlineAddressMap = plugin.getOnlineAddressMap();
        this.onlineTimeMap = plugin.getOnlineTimeMap();
    }

    @Override
    public void run() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (this.onlineTimeMap.containsKey(player.getUniqueId())) {
                final int onlineSeconds = this.onlineTimeMap.get(player.getUniqueId()) + 1;

                // Test whether or not the player has been online for long enough.
                if (onlineSeconds >= this.settings.getPayoutInterval()) {
                    this.onlineTimeMap.remove(player.getUniqueId());
                    this.pay(player);
                } else {
                    this.onlineTimeMap.put(player.getUniqueId(), onlineSeconds);
                }
            } else {
                // Add the player to the map with 1 second of online time.
                this.onlineTimeMap.put(player.getUniqueId(), 1);
            }
        }
    }

    private void pay(final Player player) {
        // Test if the player has hit the payout cap, if enabled.
        if (!player.hasPermission("chronopay.bypass.cap") &&
            this.settings.getPayoutCap() > 0.0 &&
            this.payedMoneyMap.containsKey(player.getUniqueId()) &&
            this.payedMoneyMap.get(player.getUniqueId()) >= settings.getPayoutCap()
        ) {
            if (!this.settings.getCapReachedMessage().equalsIgnoreCase("none")) {
                player.sendMessage(this.settings.getCapReachedMessage());
            }
            return;
        }

        // Test if the player is logged in from multiple locations.
        if (!player.hasPermission("chronopay.bypass.ip") &&
            Collections.frequency(this.onlineAddressMap.values(), player.getAddress().getHostString()) > 1
        ) {
            if (!this.settings.getIPMessage().equalsIgnoreCase("none")) {
                player.sendMessage(this.settings.getIPMessage());
            }
            return;
        }

        // Test if the player is AFK.
        final IEssentials essentials = this.plugin.getEssentials();
        if (!player.hasPermission("chronopay.bypass.afk") &&
            essentials != null &&
            essentials.isEnabled() &&
            essentials.getUser(player).isAfk()
        ) {
            if (!this.settings.getAfkMessage().equalsIgnoreCase("none")) {
                player.sendMessage(this.settings.getAfkMessage());
            }
            return;
        }

        // Pay the player the configured amount.
        this.plugin.getEconomy().depositPlayer(player, this.settings.getPayoutAmount());
        if (!this.settings.getPayoutMessage().equalsIgnoreCase("none")) {
            player.sendMessage(
                this.settings.getPayoutMessage()
                    .replace("{money}", String.valueOf(this.settings.getPayoutAmount()))
                    .replace("{seconds}", String.valueOf(this.settings.getPayoutInterval()))
                    .replace("{minutes}", String.valueOf(this.settings.getPayoutInterval() / 60))
            );
        }

        // Update the payed money map to reflect the player's new earnings.
        final UUID uuid = player.getUniqueId();
        if (this.payedMoneyMap.containsKey(uuid)) {
            this.payedMoneyMap.put(uuid, this.payedMoneyMap.get(uuid) + this.settings.getPayoutAmount());
        } else {
            this.payedMoneyMap.put(uuid, this.plugin.getSettings().getPayoutAmount());
        }
    }
}