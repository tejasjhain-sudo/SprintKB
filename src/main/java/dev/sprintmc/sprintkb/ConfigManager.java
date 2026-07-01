package dev.sprintmc.sprintkb;

import org.bukkit.configuration.ConfigurationSection;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final SprintKBPlugin plugin;
    private final Map<String, KnockbackProfile> profiles = new HashMap<>();
    
    public ConfigManager(SprintKBPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void loadConfig() {
        plugin.reloadConfig();
        profiles.clear();
        
        ConfigurationSection profilesSection = plugin.getConfig().getConfigurationSection("profiles");
        if (profilesSection != null) {
            for (String key : profilesSection.getKeys(false)) {
                ConfigurationSection p = profilesSection.getConfigurationSection(key);
                KnockbackProfile profile = new KnockbackProfile(
                    key,
                    p.getDouble("horizontal", 0.4),
                    p.getDouble("vertical", 0.34),
                    p.getDouble("horizontal-cap", 1.0),
                    p.getDouble("vertical-cap", 0.42),
                    p.getDouble("min-horizontal", 0.22),
                    p.getDouble("ground-multiplier", 1.0),
                    p.getDouble("air-multiplier", 0.82),
                    p.getDouble("sprint-horizontal-bonus", 0.06),
                    p.getDouble("sprint-vertical-bonus", 0.015),
                    p.getDouble("critical-hit-modifier", 1.0),
                    p.getDouble("shield-hit-modifier", 0.92),
                    p.getBoolean("combo.enabled", true),
                    p.getInt("combo.max-combo-ticks", 18),
                    p.getDouble("combo.vertical-reduction-per-hit", 0.018),
                    p.getDouble("combo.min-vertical", 0.28),
                    p.getDouble("combo.horizontal-bonus-per-hit", 0.0),
                    p.getDouble("combo.max-horizontal-bonus", 0.0),
                    p.contains("ping-compensation.enabled") ? p.getBoolean("ping-compensation.enabled") : null
                );
                profiles.put(key, profile);
            }
        }
    }
    
    public Map<String, KnockbackProfile> getProfiles() {
        return profiles;
    }
    
    public double getWeaponModifier(String weapon) {
        if (!plugin.getConfig().getBoolean("weapon-modifiers.enabled", true)) return 1.0;
        return plugin.getConfig().getDouble("weapon-modifiers." + weapon, 1.0);
    }
}
