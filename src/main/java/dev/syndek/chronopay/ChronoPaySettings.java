package dev.syndek.chronopay;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class ChronoPaySettings {
    private final ChronoPayPlugin plugin;

    private int    payoutInterval;
    private float  payoutAmount;
    private float  payoutCap;
    private String payoutMessage;
    private String capReachedMessage;
    private String afkMessage;
    private String ipMessage;

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

    public String getPayoutMessage() {
        return this.payoutMessage;
    }

    public String getCapReachedMessage() {
        return this.capReachedMessage;
    }

    public String getAfkMessage() {
        return this.afkMessage;
    }

    public String getIPMessage() {
        return this.ipMessage;
    }

    public void load() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();

        final Configuration config = this.plugin.getConfig();
        this.payoutInterval = config.getInt("payout-interval", 60);
        this.payoutAmount = (float) config.getDouble("payout-amount", 0.1);
        this.payoutCap = (float) config.getDouble("payout-cap", 5.0);
        this.payoutMessage = this.getMessage("payout", "&7You have received &a{money} &7for {minutes} minutes online time!");
        this.capReachedMessage = this.getMessage("cap-reached", "&cYou have reached payout the limit for today!");
        this.afkMessage = this.getMessage("afk", "&cYou haven't earned any money because you're AFK!");
        this.ipMessage = this.getMessage("ip", "&cYou haven't earned any money because you're logged in on multiple accounts!");
    }

    private String getMessage(String name, String def) {
        return ChatColor.translateAlternateColorCodes('&', this.plugin.getConfig().getString("messages." + name, def));
    }
}