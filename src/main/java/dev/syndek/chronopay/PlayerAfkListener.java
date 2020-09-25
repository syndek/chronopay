package dev.syndek.chronopay;

import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class PlayerAfkListener implements Listener {
    private final ChronoPayPlugin plugin;

    public PlayerAfkListener(final ChronoPayPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void onAfkStatusChange(AfkStatusChangeEvent event) {
        final Player player = event.getAffected().getBase();

        this.plugin.getPlayerTracker().setPlayerAfkStatus(player, event.getValue());

        if (this.plugin.getPlayerTracker().playerFailsAfkCheck(player)) {
            final String goneAfkMessage = this.plugin.getSettings().getGoneAfkMessage();
            if (!goneAfkMessage.isEmpty()) {
                player.sendMessage(goneAfkMessage);
            }
        }
    }
}