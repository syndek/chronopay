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

import com.google.common.collect.ImmutableMap;
import dev.syndek.chronopay.ChronoPayPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChronoPayCommandHandler implements CommandExecutor, TabCompleter {
    private static final Map<String, ChronoPayCommand> COMMAND_MAP =
        new ImmutableMap.Builder<String, ChronoPayCommand>()
            .put("help", HelpCommand.INSTANCE)
            .build();

    private final ChronoPayPlugin plugin;

    public ChronoPayCommandHandler(final @NotNull ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        final @NotNull CommandSender sender,
        final @NotNull Command command,
        final @NotNull String label,
        final @NotNull String[] args
    ) {
        final ChronoPayCommand commandToExecute;

        if (args.length == 0) {
            commandToExecute = HelpCommand.INSTANCE;
        } else {
            commandToExecute = getCommandByName(args[0]);
        }

        if (commandToExecute == null) {
            sender.sendMessage("Unknown command! Type /chronopay help for assistance.");
        } else {
            final List<String> newArgs = args.length == 0
                ? Collections.emptyList()
                : Arrays.asList(args).subList(1, args.length);

            commandToExecute.execute(plugin, sender, newArgs);
        }

        return true;
    }

    @Override
    @NotNull
    public List<String> onTabComplete(
        final @NotNull CommandSender sender,
        final @NotNull Command command,
        final @NotNull String alias,
        final @NotNull String[] args
    ) {
        // TODO: Return tab complete options for correct command.
        return Collections.emptyList();
    }

    @Nullable
    private static ChronoPayCommand getCommandByName(final @NotNull String commandName) {
        return COMMAND_MAP.getOrDefault(commandName.toLowerCase(), null);
    }
}