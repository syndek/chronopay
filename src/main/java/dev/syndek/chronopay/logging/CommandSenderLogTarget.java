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

package dev.syndek.chronopay.logging;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandSenderLogTarget implements LogTarget {
    private final CommandSender commandSender;
    private final Logger consoleLogger;

    public CommandSenderLogTarget(
        final @NotNull CommandSender commandSender,
        final @NotNull Logger consoleLogger
    ) {
        this.commandSender = commandSender;
        this.consoleLogger = consoleLogger;
    }

    @Override
    public void write(@NotNull Level level, @NotNull String message) {
        if (commandSender instanceof ConsoleCommandSender) {
            consoleLogger.log(level, message);
        } else {
            commandSender.sendMessage(message);
        }
    }
}