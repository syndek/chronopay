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

package dev.syndek.chronopay.command;

import dev.syndek.chronopay.ChronoPayPlugin;
import dev.syndek.chronopay.logging.CommandSenderLogTarget;
import dev.syndek.chronopay.logging.LogTarget;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public final class ReloadCommand implements ChronoPayCommand {
    public static final ReloadCommand INSTANCE = new ReloadCommand();

    private ReloadCommand() { }

    @Override
    public void execute(
        final @NotNull ChronoPayPlugin plugin,
        final @NotNull CommandSender sender,
        final @NotNull List<String> args
    ) {
        final LogTarget target = new CommandSenderLogTarget(sender, plugin.getLogger());

        try {
            plugin.getSettings().load(target);
        } catch (final InvalidConfigurationException exception) {
            target.write(Level.SEVERE, "Failed to load configuration. Please correct any errors and try again.");
        }
    }

    @Override
    @NotNull
    public List<String> getTabCompleteOptions(
        final @NotNull ChronoPayPlugin plugin,
        final @NotNull CommandSender sender,
        final @NotNull List<String> args
    ) {
        return Collections.emptyList();
    }
}