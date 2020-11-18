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

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChronoPaySettings {
    private final ChronoPayPlugin plugin;

    private int          payoutInterval;
    private int          payoutCycleResetInterval;
    private float        payoutAmount;
    private List<String> payoutCommands;
    private int          payoutCap;
    private boolean      checkAddress;
    private boolean      checkAfk;
    private boolean      checkCap;
    private String       payoutMessage;
    private String       cycleResetMessage;
    private String       capReachedMessage;
    private String       multipleAccountsMessage;
    private String       goneAfkMessage;

    public ChronoPaySettings(final @NotNull ChronoPayPlugin plugin) {
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

    public @NotNull List<String> getPayoutCommands() {
        return this.payoutCommands;
    }

    public int getPayoutCap() {
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

    public @NotNull String getPayoutMessage() {
        return this.payoutMessage;
    }

    public @NotNull String getCycleResetMessage() {
        return this.cycleResetMessage;
    }

    public @NotNull String getCapReachedMessage() {
        return this.capReachedMessage;
    }

    public @NotNull String getMultipleAccountsMessage() {
        return this.multipleAccountsMessage;
    }

    public @NotNull String getGoneAfkMessage() {
        return this.goneAfkMessage;
    }

    public void load() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();

        final Configuration config = this.plugin.getConfig();
        this.payoutInterval = config.getInt("payout-interval", 600);
        this.payoutCycleResetInterval = config.getInt("payout-cycle-reset-interval", 86400);
        this.payoutAmount = (float) config.getDouble("payout-amount", 0.1);
        this.payoutCommands = config.getStringList("payout-commands");
        this.payoutCap = config.getInt("payout-cap", 50);
        this.checkAddress = config.getBoolean("checks.address", true);
        this.checkAfk = config.getBoolean("checks.afk", true);
        this.checkCap = config.getBoolean("checks.cap", true);
        this.payoutMessage = this.getMessage("messages.payout")
            .replace("{money}", Float.toString(this.payoutAmount))
            .replace("{minutes}", String.format("%.02f", (float) this.payoutInterval / 60))
            .replace("{seconds}", Integer.toString(this.payoutInterval));
        this.cycleResetMessage = this.getMessage("messages.cycle-reset");
        this.capReachedMessage = this.getMessage("messages.cap-reached");
        this.multipleAccountsMessage = this.getMessage("messages.multiple-accounts");
        this.goneAfkMessage = this.getMessage("messages.gone-afk");
    }

    private @NotNull String getMessage(final @NotNull String key) {
        //noinspection ConstantConditions
        return ChatColor.translateAlternateColorCodes(
            '&',
            this.plugin.getConfig().getString(key, "")
        );
    }
}