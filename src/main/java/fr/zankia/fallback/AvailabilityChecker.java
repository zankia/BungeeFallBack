package fr.zankia.fallback;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AvailabilityChecker {
    private final Plugin plugin;

    private Map<ServerInfo, Collection<ProxiedPlayer>> waitingList = new HashMap<>();
    private ScheduledTask scheduledTask;

    public AvailabilityChecker(Plugin plugin) {
        this.plugin = plugin;
    }

    public void addWaitingList(ServerInfo server, ProxiedPlayer player) {
        if (waitingList.isEmpty()) {
            startScheduler();
        }

        waitingList.computeIfAbsent(server, k -> new HashSet<>()).add(player);
        plugin.getLogger().info(waitingList.toString());
    }

    public void removeWaitingList(ServerInfo server) {
        waitingList.remove(server);
        if (waitingList.isEmpty()) {
            plugin.getLogger().info("Stopping Scheduler");
            scheduledTask.cancel();
        }
    }

    public void announcePlayers(ServerInfo server) {
        waitingList.get(server).forEach(player -> {
            TextComponent message = new TextComponent(server.getName() + " is back online");
            message.setColor(ChatColor.DARK_GREEN);
            player.sendMessage(ChatMessageType.CHAT, message);
        });
        removeWaitingList(server);
    }

    private void startScheduler() {
        plugin.getLogger().info("Starting Scheduler");
        scheduledTask = plugin.getProxy().getScheduler().schedule(
            plugin,
            () -> waitingList.keySet().forEach(server -> {
                plugin.getLogger().info("Checking " + server.getName());
                server.ping((result, error) -> {
                    if (error == null) {
                        announcePlayers(server);
                    }
                });
            }),
            10,
            10,
            TimeUnit.SECONDS
        );
    }
}
