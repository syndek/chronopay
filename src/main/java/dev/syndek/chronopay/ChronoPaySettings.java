package dev.syndek.chronopay;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class ChronoPaySettings {
    private final ChronoPayPlugin plugin;

    private int     payoutInterval;
    private int     payoutCycleResetInterval;
    private float   payoutAmount;
    private float   payoutCap;
    private boolean checkAddress;
    private boolean checkAfk;
    private boolean checkCap;
    private String  payoutMessage;
    private String  cycleResetMessage;
    private String  capReachedMessage;
    private String  multipleAccountsMessage;
    private String  goneAfkMessage;

    public ChronoPaySettings(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    public int getPayoutInterval() {
        return this.payoutInterval;
    }

    public int getPayoutCycleResetInterval() {
        return this.payoutCycleResetInterval;
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

    public boolean checkCap() {
        return this.checkCap;
    }

    public String getPayoutMessage() {
        return this.payoutMessage;
    }

    public String getCycleResetMessage() {
        return this.cycleResetMessage;
    }

    public String getCapReachedMessage() {
        return this.capReachedMessage;
    }

    public String getMultipleAccountsMessage() {
        return this.multipleAccountsMessage;
    }

    public String getGoneAfkMessage() {
        return this.goneAfkMessage;
    }

    public void load() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();

        final Configuration config = this.plugin.getConfig();
        this.payoutInterval = config.getInt("payout-interval", 60);
        this.payoutCycleResetInterval = config.getInt("payout-cycle-reset-interval", 60);
        this.payoutAmount = (float) config.getDouble("payout-amount", 0.1);
        this.payoutCap = (float) config.getDouble("payout-cap", 5.0);
        this.checkAddress = config.getBoolean("checks.address", true);
        this.checkAfk = config.getBoolean("checks.afk", true);
        this.checkCap = config.getBoolean("checks.cap", true);
        this.payoutMessage = this.getMessage("messages.payout")
            .replace("{money}", Float.toString(this.payoutAmount))
            .replace("{minutes}", Float.toString((float) this.payoutInterval / 60))
            .replace("{seconds}", Integer.toString(this.payoutInterval));
        this.cycleResetMessage = this.getMessage("messages.cycle-reset");
        this.capReachedMessage = this.getMessage("messages.cap-reached");
        this.multipleAccountsMessage = this.getMessage("messages.multiple-accounts");
        this.goneAfkMessage = this.getMessage("messages.gone-afk");
    }

    private String getMessage(final String key) {
        return ChatColor.translateAlternateColorCodes(
            '&',
            this.plugin.getConfig().getString(key, "")
        );
    }
}