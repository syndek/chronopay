package dev.syndek.chronopay;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChronoPayCommandExecutor implements CommandExecutor {
    private final ChronoPayPlugin plugin;

    public ChronoPayCommandExecutor(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
        final CommandSender sender,
        final Command command,
        final String label,
        final String[] args
    ) {
        if (!sender.hasPermission("chronopay.reload")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this.");
        } else {
            this.plugin.getSettings().load();
            sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
        }
        return true;
    }
}