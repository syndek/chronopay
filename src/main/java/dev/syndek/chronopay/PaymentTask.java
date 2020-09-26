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
                final float payoutAmount = this.settings.getPayoutAmount();

                // Pay the player the configured amount.
                this.plugin.getEconomy().depositPlayer(player, payoutAmount);

                data.addPayedMoney(payoutAmount);
                data.resetOnlineTime();

                final String payoutMessage = this.settings.getPayoutMessage();
                if (!payoutMessage.isEmpty()) {
                    player.sendMessage(
                        payoutMessage
                            .replace("{money}", Float.toString(payoutAmount))
                            .replace("{minutes}", Float.toString((float) this.settings.getPayoutInterval() / 60))
                            .replace("{seconds}", Integer.toString(this.settings.getPayoutInterval()))
                    );
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