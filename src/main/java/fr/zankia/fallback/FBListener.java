package fr.zankia.fallback;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Iterator;

public class FBListener implements Listener {
    private final Plugin plugin;
    private final AvailabilityChecker checker;

    public FBListener(Plugin plugin) {
        this.plugin = plugin;
        this.checker = new AvailabilityChecker(plugin);
    }

    @EventHandler
    public void onServerKickEvent(ServerKickEvent evt) {
        if (!BaseComponent.toPlainText(evt.getKickReasonComponent()).equalsIgnoreCase("server closed")) {
            return;
        }

        Iterator<ServerInfo> serversIterator = plugin.getProxy().getServers().values().stream().iterator();
        ServerInfo fallbackServer = serversIterator.next();
        while (evt.getKickedFrom().getName().equals(fallbackServer.getName()) && serversIterator.hasNext()) {
            fallbackServer = serversIterator.next();
        }

        evt.setCancelServer(fallbackServer);
        evt.setCancelled(true);

        plugin.getLogger().info("Redirecting " + evt.getPlayer().getDisplayName() +
                " from " + evt.getKickedFrom().getName() + " to " + evt.getCancelServer().getName());

        checker.addWaitingList(evt.getKickedFrom(), evt.getPlayer());
    }
}
