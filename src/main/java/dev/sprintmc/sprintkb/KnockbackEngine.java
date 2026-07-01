package dev.sprintmc.sprintkb;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.GameMode;

public class KnockbackEngine {
    
    private final SprintKBPlugin plugin;
    private final ProfileManager profileManager;
    private final PingTracker pingTracker;
    private final ComboTracker comboTracker;
    private final LagMonitor lagMonitor;
    private final ConfigManager config;
    private final DebugManager debugManager;
    
    public KnockbackEngine(SprintKBPlugin plugin, ProfileManager profileManager, PingTracker pingTracker,
                           ComboTracker comboTracker, LagMonitor lagMonitor, ConfigManager config, DebugManager debugManager) {
        this.plugin = plugin;
        this.profileManager = profileManager;
        this.pingTracker = pingTracker;
        this.comboTracker = comboTracker;
        this.lagMonitor = lagMonitor;
        this.config = config;
        this.debugManager = debugManager;
    }
    
    public Vector calculateKnockback(Player victim, Player attacker, Vector originalKnockback, String weaponType) {
        if (!plugin.getConfig().getBoolean("compatibility.ignore-creative", true) || victim.getGameMode() != GameMode.CREATIVE) {
             if (plugin.getConfig().getBoolean("compatibility.ignore-spectator", true) && victim.getGameMode() == GameMode.SPECTATOR) {
                 return originalKnockback;
             }
        } else {
            return originalKnockback;
        }
        
        KnockbackProfile profile = profileManager.getProfile(victim);
        if (profile == null) {
            return originalKnockback;
        }
        Vector finalKb = new Vector(0, 0, 0);
        
        // Base Calculation
        double horizontal = profile.getHorizontal();
        double vertical = profile.getVertical();
        
        // Apply weapon modifier
        double weaponModifier = config.getWeaponModifier(weaponType);
        horizontal *= weaponModifier;
        vertical *= weaponModifier;
        
        // Direction
        Vector direction = attacker.getLocation().getDirection().setY(0).normalize();
        
        // Sprint Bonus
        if (attacker.isSprinting()) {
            horizontal += profile.getSprintHorizontalBonus();
            vertical += profile.getSprintVerticalBonus();
        }
        
        // Ground/Air Modifiers
        if (((org.bukkit.entity.Entity) victim).isOnGround()) {
            horizontal *= profile.getGroundMultiplier();
            vertical *= profile.getGroundMultiplier();
        } else {
            horizontal *= profile.getAirMultiplier();
            vertical *= profile.getAirMultiplier();
        }
        
        // Shield Hit
        if (victim.isBlocking()) {
            horizontal *= profile.getShieldHitModifier();
            vertical *= profile.getShieldHitModifier();
        }
        
        // Combo Smoothing
        if (profile.isComboEnabled()) {
            int combo = comboTracker.getCombo(victim, profile.getMaxComboTicks());
            if (combo > 1) {
                vertical -= (combo - 1) * profile.getVerticalReductionPerHit();
                vertical = Math.max(vertical, profile.getComboMinVertical());
            }
        }
        
        // Lag & Ping Compensation
        boolean isLagging = lagMonitor.isLagging();
        boolean disablePingCompWhenLagging = plugin.getConfig().getBoolean("lag-safety.disable-ping-compensation-when-lagging", true);
        
        if (plugin.getConfig().getBoolean("ping-compensation.enabled", true) && (!isLagging || !disablePingCompWhenLagging)) {
            int attackerPing = pingTracker.getAveragePing(attacker);
            int pingOffset = plugin.getConfig().getInt("ping-compensation.ping-offset", 25);
            int maxCompMs = plugin.getConfig().getInt("ping-compensation.max-compensation-ms", 120);
            
            if (attackerPing > pingOffset && !pingTracker.hasPingSpike(attacker)) {
                int compMs = Math.min(attackerPing - pingOffset, maxCompMs);
                double compRatio = (double) compMs / maxCompMs;
                
                double extraH = plugin.getConfig().getDouble("ping-compensation.max-extra-horizontal", 0.04) * compRatio;
                double extraV = plugin.getConfig().getDouble("ping-compensation.max-extra-vertical", 0.02) * compRatio;
                
                horizontal += extraH;
                vertical += extraV;
            }
        }
        
        // Apply caps and minimums
        horizontal = Math.max(profile.getMinHorizontal(), Math.min(horizontal, profile.getHorizontalCap()));
        vertical = Math.min(vertical, profile.getVerticalCap());
        
        // Global caps from compatibility
        double globalMaxH = plugin.getConfig().getDouble("compatibility.max-final-horizontal", 1.05);
        double globalMaxV = plugin.getConfig().getDouble("compatibility.max-final-vertical", 0.45);
        horizontal = Math.min(horizontal, globalMaxH);
        vertical = Math.min(vertical, globalMaxV);
        
        // Set final vector
        finalKb.setX(direction.getX() * horizontal);
        finalKb.setZ(direction.getZ() * horizontal);
        finalKb.setY(vertical);
        
        // Debugging
        if (debugManager.isDebugEnabled()) {
            debugManager.logDebug(attacker, victim, profile.getName(), attacker.getPing(), victim.getPing(),
                pingTracker.getAveragePing(attacker), originalKnockback, finalKb,
                comboTracker.getCombo(victim, profile.getMaxComboTicks()), ((org.bukkit.entity.Entity) victim).isOnGround(), isLagging);
        }
        
        return finalKb;
    }
}
