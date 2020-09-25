package dev.syndek.chronopay;

import org.bukkit.configuration.Configuration;

public class ChronoPaySettings {
    private final ChronoPayPlugin plugin;

    private int     payoutInterval;
    private float   payoutAmount;
    private float   payoutCap;
    private boolean checkAddress;
    private boolean checkAfk;

    public ChronoPaySettings(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    public int getPayoutInterval() {
        return this.payoutInterval;
    }

    public float getPayoutAmount() {
        return this.payoutAmount;
    }

    public float getPayoutCap() {
        return this.payoutCap;
    }

    public boolean checkAddress() {
        return this.checkAddress;
    }

    public boolean checkAfk() {
        return this.checkAfk;
    }

    public void load() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();

        final Configuration config = this.plugin.getConfig();
        this.payoutInterval = config.getInt("payout-interval", 60);
        this.payoutAmount = (float) config.getDouble("payout-amount", 0.1);
        this.payoutCap = (float) config.getDouble("payout-cap", 5.0);
        this.checkAddress = config.getBoolean("checks.address", true);
        this.checkAfk = config.getBoolean("checks.afk", true);
    }
}