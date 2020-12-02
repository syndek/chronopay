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

import dev.syndek.chronopay.api.ChronoPay;
import dev.syndek.chronopay.command.ChronoPayCommandHandler;
import dev.syndek.chronopay.logging.LogTarget;
import dev.syndek.chronopay.logging.LoggerLogTarget;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChronoPayPlugin extends JavaPlugin implements ChronoPay {
    private final ChronoPaySettings settings = new ChronoPaySettings(this);
    private final LogTarget logTarget = new LoggerLogTarget(getLogger());

    @Override
    public void onEnable() {
        final ChronoPayCommandHandler handler = new ChronoPayCommandHandler(this);
        final PluginCommand command = Objects.requireNonNull(getCommand("chronopay"));
        command.setExecutor(handler);
        command.setTabCompleter(handler);
    }

    @Override
    public void onDisable() {

    }

    @Override
    @NotNull
    public ChronoPaySettings getSettings() {
        return settings;
    }

    @NotNull
    public LogTarget getLogTarget() {
        return logTarget;
    }
}