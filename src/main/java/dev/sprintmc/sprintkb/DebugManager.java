package dev.sprintmc.sprintkb;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class DebugManager {
    
    private boolean debugEnabled = false;
    
    public void setDebugEnabled(boolean enabled) {
        this.debugEnabled = enabled;
    }
    
    public boolean isDebugEnabled() {
        return debugEnabled;
    }
    
    public void logDebug(Player attacker, Player victim, String profile, int attackerPing, int victimPing, 
                         int rollingPing, Vector originalKb, Vector finalKb, int comboCount, 
                         boolean victimOnGround, boolean serverLagging) {
        if (!debugEnabled) return;
        
        String msg = ChatColor.GRAY + "[" + ChatColor.AQUA + "SprintKB Debug" + ChatColor.GRAY + "] " +
                     ChatColor.WHITE + attacker.getName() + " -> " + victim.getName() + "\n" +
                     ChatColor.GRAY + " Profile: " + ChatColor.WHITE + profile + "\n" +
                     ChatColor.GRAY + " Attacker Ping: " + ChatColor.WHITE + attackerPing + "ms (Avg: " + rollingPing + "ms)\n" +
                     ChatColor.GRAY + " Victim Ping: " + ChatColor.WHITE + victimPing + "ms\n" +
                     ChatColor.GRAY + " Original KB: " + ChatColor.WHITE + formatVector(originalKb) + "\n" +
                     ChatColor.GRAY + " Final KB: " + ChatColor.WHITE + formatVector(finalKb) + "\n" +
                     ChatColor.GRAY + " Combo: " + ChatColor.WHITE + comboCount + "\n" +
                     ChatColor.GRAY + " Victim Grounded: " + ChatColor.WHITE + victimOnGround + "\n" +
                     ChatColor.GRAY + " Server Lagging: " + ChatColor.WHITE + serverLagging;
                     
        // Send to ops
        for (Player p : attacker.getServer().getOnlinePlayers()) {
            if (p.hasPermission("sprintkb.debug")) {
                p.sendMessage(msg);
            }
        }
    }
    
    private String formatVector(Vector v) {
        return String.format("%.3f, %.3f, %.3f", v.getX(), v.getY(), v.getZ());
    }
}
