package dev.syndek.chronopay;

import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class PlayerAfkListener implements Listener {
    private final ChronoPayPlugin plugin;
    private final PlayerTracker   playerTracker;

    public PlayerAfkListener(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
        this.playerTracker = plugin.getPlayerTracker();
    }

    @EventHandler
    private void onAfkStatusChange(AfkStatusChangeEvent event) {
        final Player player = event.getAffected().getBase();

        this.playerTracker.setPlayerAfkStatus(player, event.getValue());
        this.playerTracker.recalculatePlayerValidity(player);

        if (this.playerTracker.playerFailsAfkCheck(player)) {
            final String goneAfkMessage = this.plugin.getSettings().getGoneAfkMessage();
            if (!goneAfkMessage.isEmpty()) {
                player.sendMessage(goneAfkMessage);
            }
        }
    }
}