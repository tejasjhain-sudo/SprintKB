package dev.sprintmc.sprintkb;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager {
    private final ConfigManager configManager;
    private final Map<UUID, String> playerProfiles = new HashMap<>();
    private final Map<String, String> worldProfiles = new HashMap<>();

    public ProfileManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public KnockbackProfile getProfile(Player player) {
        String profileName = "default";

        if (playerProfiles.containsKey(player.getUniqueId())) {
            profileName = playerProfiles.get(player.getUniqueId());
        } else if (worldProfiles.containsKey(player.getWorld().getName())) {
            profileName = worldProfiles.get(player.getWorld().getName());
        } else {
            // Check permissions
            for (String key : configManager.getProfiles().keySet()) {
                if (player.hasPermission("sprintkb.profile." + key)) {
                    profileName = key;
                    break; // Use highest priority permission (depends on config order but simple enough)
                }
            }
        }

        return configManager.getProfiles().getOrDefault(profileName, configManager.getProfiles().get("default"));
    }

    public void setPlayerProfile(Player player, String profileName) {
        if (configManager.getProfiles().containsKey(profileName)) {
            playerProfiles.put(player.getUniqueId(), profileName);
        } else {
            playerProfiles.remove(player.getUniqueId());
        }
    }

    public void setWorldProfile(String worldName, String profileName) {
        if (configManager.getProfiles().containsKey(profileName)) {
            worldProfiles.put(worldName, profileName);
        } else {
            worldProfiles.remove(worldName);
        }
    }

    public void removePlayer(Player player) {
        playerProfiles.remove(player.getUniqueId());
    }
}
