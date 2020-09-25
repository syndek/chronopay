package dev.syndek.chronopay;

import net.ess3.api.IEssentials;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChronoPayPlugin extends JavaPlugin {
    private final ChronoPaySettings settings      = new ChronoPaySettings(this);
    private final PlayerTracker     playerTracker = new PlayerTracker(this);
    private       Economy           economy;
    private       IEssentials       essentials;

    @Override
    public void onEnable() {
        // Hook Vault.
        final RegisteredServiceProvider<Economy> economyProvider = this.getServer()
            .getServicesManager()
            .getRegistration(Economy.class);

        if (economyProvider != null) {
            this.economy = economyProvider.getProvider();
            this.getLogger().info("Successfully hooked Vault economy!");
        } else {
            this.getLogger().severe("Failed to hook Vault economy!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Hook Essentials, if available.
        final Plugin essentialsPlugin = this.getServer().getPluginManager().getPlugin("Essentials");
        if (essentialsPlugin != null) {
            this.essentials = (IEssentials) essentialsPlugin;
            this.getLogger().info("Successfully hooked Essentials!");
        } else {
            this.getLogger().warning("Failed to hook Essentials! AFK checking will not work.");
        }

        this.settings.load();
        this.getCommand("cpreload").setExecutor(new ChronoPayCommandExecutor(this));
        this.getServer().getScheduler().runTaskTimer(this, new PaymentTask(this), 0, 20);
        this.getServer().getScheduler().runTaskTimer(
            this,
            new PayoutCycleResetTask(this),
            0,
            this.settings.getPayoutCycleResetInterval()
        );
        this.getServer().getPluginManager().registerEvents(new PlayerAfkListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
    }

    public ChronoPaySettings getSettings() {
        return this.settings;
    }

    public PlayerTracker getPlayerTracker() {
        return this.playerTracker;
    }

    public IEssentials getEssentials() {
        return this.essentials;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}