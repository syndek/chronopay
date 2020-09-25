package dev.syndek.chronopay;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerConnectionListener implements Listener {
    private final ChronoPayPlugin plugin;

    public PlayerConnectionListener(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onPlayerJoin(final PlayerJoinEvent event) {
        this.plugin.getPlayerTracker().addPlayerAddress(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(final PlayerQuitEvent event) {
        this.plugin.getPlayerTracker().removePlayerAddress(event.getPlayer());
    }
}