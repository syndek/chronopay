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

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ChronoPayPlugin extends JavaPlugin {
    private final ChronoPaySettings settings      = new ChronoPaySettings(this);
    private final PlayerTracker     playerTracker = new PlayerTracker(this);
    private       Economy           economy;

    @Override
    public void onEnable() {
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

    public @NotNull ChronoPaySettings getSettings() {
        return this.settings;
    }

    public @NotNull PlayerTracker getPlayerTracker() {
        return this.playerTracker;
    }

    public @NotNull Economy getEconomy() {
        return this.economy;
    }
}