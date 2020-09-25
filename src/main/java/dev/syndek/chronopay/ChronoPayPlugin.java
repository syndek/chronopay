package dev.syndek.chronopay;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class ChronoPayPlugin extends JavaPlugin {
    private final ChronoPaySettings settings      = new ChronoPaySettings(this);
    private final PlayerTracker     playerTracker = new PlayerTracker(this);
    private       Economy           economy;

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

        this.settings.load();
        this.getCommand("cpreload").setExecutor(new ChronoPayCommandExecutor(this));
        this.getServer().getScheduler().runTaskTimer(this, new PaymentTask(this), 0, 20);
        this.getServer().getScheduler().runTaskTimer(
            this,
            new PayoutCycleResetTask(this),
            0,
            this.settings.getPayoutCycleResetInterval() * 20
        );
        this.getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        if (this.getServer().getPluginManager().getPlugin("Essentials") != null) {
            this.getServer().getPluginManager().registerEvents(new PlayerAfkListener(this), this);
        }
    }

    public ChronoPaySettings getSettings() {
        return this.settings;
    }

    public PlayerTracker getPlayerTracker() {
        return this.playerTracker;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}