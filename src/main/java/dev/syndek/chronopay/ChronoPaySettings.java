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

import dev.syndek.chronopay.api.Settings;
import dev.syndek.chronopay.logging.LogTarget;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ChronoPaySettings implements Settings {
    private static final int TICKS_PER_SECOND = 20;
    private static final int TICKS_PER_MINUTE = TICKS_PER_SECOND * 60;
    private static final int TICKS_PER_HOUR   = TICKS_PER_MINUTE * 60;

    private final ChronoPayPlugin plugin;

    private int  payoutCap;
    private long payoutCapResetInterval;
    private long payoutInterval;

    public ChronoPaySettings(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() throws InvalidConfigurationException {
        load(plugin.getLogTarget());
    }

    @Override
    public int getPayoutCap() {
        return payoutCap;
    }

    @Override
    public long getPayoutCapResetInterval() {
        return payoutCapResetInterval;
    }

    @Override
    public long getPayoutInterval() {
        return payoutInterval;
    }

    public void load(final @NotNull LogTarget target) throws InvalidConfigurationException {
        // Ensure the configuration file exists and is loaded in memory.
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        target.write(Level.INFO, "Loading ChronoPay configuration...");

        // Store the values locally until the entire configuration has been parsed.
        // We don't want to modify the loaded configuration state if there's an error in the file.
        int payoutCap = loadPayoutCap(plugin.getConfig(), target);
        long payoutCapResetInterval = loadPayoutCapResetInterval(plugin.getConfig(), target);
        long payoutInterval = loadPayoutInterval(plugin.getConfig(), target);

        // At this point, it's safe to assume that the configuration was loaded without errors.
        this.payoutCap = payoutCap;
        this.payoutCapResetInterval = payoutCapResetInterval;
        this.payoutInterval = payoutInterval;

        target.write(Level.INFO, "Configuration loaded successfully.");
    }

    private int loadPayoutCap(
        final @NotNull Configuration configuration,
        final @NotNull LogTarget target
    ) throws InvalidConfigurationException {
        if (!configuration.contains("payout-cap")) {
            target.write(Level.WARNING, "No payout-cap value set. Using default (10).");
            return 10;
        }

        final int payoutCap = configuration.getInt("payout-cap");

        if (payoutCap < 0) {
            throw new InvalidConfigurationException("payout-cap value must be either 0 or a positive integer.");
        }

        if (payoutCap > 0) {
            target.write(Level.INFO, "Using payout cap of " + payoutCap + ".");
        } else {
            target.write(Level.INFO, "Using no payout cap.");
        }

        return payoutCap;
    }

    private long loadPayoutCapResetInterval(
        final @NotNull Configuration configuration,
        final @NotNull LogTarget target
    ) throws InvalidConfigurationException {
        if (!configuration.contains("payout-cap-reset-interval")) {
            target.write(Level.WARNING, "No payout-cap-reset-interval value set. Using default (24 hours).");
            return 24 * TICKS_PER_HOUR;
        }

        final String payoutCapResetInterval = configuration.getString("payout-cap-reset-interval");

        if (payoutCapResetInterval == null) {
            throw new InvalidConfigurationException("payout-cap-reset-interval value must be a valid time interval string.");
        }

        return parseTimeInterval(payoutCapResetInterval);
    }

    private long loadPayoutInterval(
        final @NotNull Configuration configuration,
        final @NotNull LogTarget target
    ) throws InvalidConfigurationException {
        if (!configuration.contains("payout-interval")) {
            target.write(Level.WARNING, "No payout-interval value set. Using default (1 hour).");
            return TICKS_PER_HOUR;
        }

        final String payoutInterval = configuration.getString("payout-interval");

        if (payoutInterval == null) {
            throw new InvalidConfigurationException("payout-interval value must be a valid time interval string.");
        }

        return parseTimeInterval(payoutInterval);
    }

    private long parseTimeInterval(final @NotNull String string) throws InvalidConfigurationException {
        long ticks = 0;

        for (final String value : string.toUpperCase().split("\\+")) {
            if (!value.matches("(\\d+)([SMH])")) {
                throw new InvalidConfigurationException("Invalid time interval: '" + value + "'");
            }

            final int timeValue;

            try {
                timeValue = Integer.parseInt(value.substring(0, value.length() - 1));
            } catch (final NumberFormatException exception) {
                throw new InvalidConfigurationException(exception);
            }

            switch (value.charAt(value.length() - 1)) {
                case 'S':
                    ticks += (long) timeValue * TICKS_PER_SECOND;
                    break;
                case 'M':
                    ticks += (long) timeValue * TICKS_PER_MINUTE;
                    break;
                case 'H':
                    ticks += (long) timeValue * TICKS_PER_HOUR;
            }
        }

        if (ticks <= 0) {
            throw new InvalidConfigurationException("Time interval value must be positive.");
        }

        return ticks;
    }
}