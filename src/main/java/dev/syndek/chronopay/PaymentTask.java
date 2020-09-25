package dev.syndek.chronopay;

import org.bukkit.entity.Player;

public final class PaymentTask implements Runnable {
    private final ChronoPayPlugin   plugin;
    private final ChronoPaySettings settings;
    private final PlayerTracker     playerTracker;

    public PaymentTask(final ChronoPayPlugin plugin) {
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
                this.pay(player);
            }
        }
    }

    private void pay(final Player player) {
        final float payoutAmount = this.settings.getPayoutAmount();

        // Pay the player the configured amount.
        this.plugin.getEconomy().depositPlayer(player, payoutAmount);

        // Update the player's total earnings to reflect the payout.
        this.playerTracker.getPlayerData(player.getUniqueId()).addPayedMoney(payoutAmount);
    }
}