package dev.syndek.chronopay;

import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class PlayerAfkListener implements Listener {
    private final ChronoPayPlugin plugin;

    public PlayerAfkListener(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onAfkStatusChange(AfkStatusChangeEvent event) {
        this.plugin.getPlayerTracker().setPlayerAfkStatus(event.getAffected().getBase(), event.getValue());
    }
}