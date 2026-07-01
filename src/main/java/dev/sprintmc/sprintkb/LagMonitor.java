package dev.sprintmc.sprintkb;

import org.bukkit.Bukkit;

public class LagMonitor {
    
    private final SprintKBPlugin plugin;
    private final ConfigManager config;
    
    public LagMonitor(SprintKBPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }
    
    public boolean isLagging() {
        if (!plugin.getConfig().getBoolean("lag-safety.enabled", true)) return false;
        
        double minTps = plugin.getConfig().getDouble("lag-safety.min-tps", 18.5);
        double[] tps = Bukkit.getTPS();
        
        if (tps[0] < minTps) {
            return true;
        }
        
        // Check MSPT if possible (Paper only)
        try {
            double mspt = Bukkit.getAverageTickTime();
            double maxMspt = plugin.getConfig().getDouble("lag-safety.max-mspt", 55.0);
            if (mspt > maxMspt) return true;
        } catch (NoSuchMethodError ignored) {
            // Server doesn't support getAverageTickTime (Spigot)
        }
        
        return false;
    }
}
