package dev.sprintmc.sprintkb;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class PingTracker implements Listener {
    private final SprintKBPlugin plugin;
    private final ConfigManager config;
    private final Map<UUID, LinkedList<Integer>> pingHistory = new HashMap<>();
    private BukkitRunnable task;

    public PingTracker(SprintKBPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
        startTask();
    }

    private void startTask() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getConfig().getBoolean("ping-compensation.enabled", true)) return;
                int maxSamples = plugin.getConfig().getInt("ping-compensation.samples", 10);
                
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    pingHistory.putIfAbsent(uuid, new LinkedList<>());
                    LinkedList<Integer> history = pingHistory.get(uuid);
                    
                    history.add(player.getPing());
                    if (history.size() > maxSamples) {
                        history.removeFirst();
                    }
                }
            }
        };
        task.runTaskTimer(plugin, 20L, 20L); // Update every second
    }

    public int getAveragePing(Player player) {
        if (!pingHistory.containsKey(player.getUniqueId()) || pingHistory.get(player.getUniqueId()).isEmpty()) {
            return player.getPing();
        }
        
        LinkedList<Integer> history = pingHistory.get(player.getUniqueId());
        int sum = 0;
        for (int p : history) {
            sum += p;
        }
        return sum / history.size();
    }

    public boolean hasPingSpike(Player player) {
        int currentPing = player.getPing();
        int avgPing = getAveragePing(player);
        int spikeThreshold = plugin.getConfig().getInt("ping-compensation.spike-threshold", 20);
        
        return (currentPing - avgPing) > spikeThreshold;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pingHistory.remove(event.getPlayer().getUniqueId());
    }

    public void cleanup() {
        if (task != null) {
            task.cancel();
        }
        pingHistory.clear();
    }
}
