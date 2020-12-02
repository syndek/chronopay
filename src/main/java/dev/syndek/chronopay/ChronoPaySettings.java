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
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ChronoPaySettings implements Settings {
    private final ChronoPayPlugin plugin;

    public ChronoPaySettings(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        load(plugin.getLogTarget());
    }

    public void load(final @NotNull LogTarget target) {
        target.write(Level.INFO, "Loading ChronoPay configuration...");
    }
}