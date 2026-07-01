package dev.sprintmc.sprintkb;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComboTracker {
    private static class ComboInfo {
        int hits;
        long lastHitTime;
    }
    
    private final Map<UUID, ComboInfo> combos = new HashMap<>();
    
    public void registerHit(Player player, int maxComboTicks) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long maxComboMillis = maxComboTicks * 50L;
        
        ComboInfo info = combos.computeIfAbsent(uuid, k -> new ComboInfo());
        
        if (now - info.lastHitTime > maxComboMillis) {
            info.hits = 1;
        } else {
            info.hits++;
        }
        
        info.lastHitTime = now;
    }
    
    public int getCombo(Player player, int maxComboTicks) {
        UUID uuid = player.getUniqueId();
        if (!combos.containsKey(uuid)) return 0;
        
        ComboInfo info = combos.get(uuid);
        long maxComboMillis = maxComboTicks * 50L;
        
        if (System.currentTimeMillis() - info.lastHitTime > maxComboMillis) {
            return 0;
        }
        return info.hits;
    }
    
    public void cleanup() {
        combos.clear();
    }
}
