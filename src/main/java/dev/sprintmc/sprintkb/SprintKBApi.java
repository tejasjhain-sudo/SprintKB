package dev.sprintmc.sprintkb;

import org.bukkit.entity.Player;

public class SprintKBApi {

    /**
     * Sets a player's knockback profile.
     * @param player The player
     * @param profile The profile name (e.g. "nethpot")
     */
    public static void setProfile(Player player, String profile) {
        if (SprintKBPlugin.getInstance() != null && SprintKBPlugin.getInstance().getProfileManager() != null) {
            SprintKBPlugin.getInstance().getProfileManager().setPlayerProfile(player, profile);
        }
    }

    /**
     * Clears a player's active profile, restoring their default behaviour.
     * @param player The player
     */
    public static void clearProfile(Player player) {
        if (SprintKBPlugin.getInstance() != null && SprintKBPlugin.getInstance().getProfileManager() != null) {
            SprintKBPlugin.getInstance().getProfileManager().removePlayer(player);
        }
    }

    /**
     * Gets the active profile name for the player.
     * @param player The player
     * @return The active KnockbackProfile object
     */
    public static KnockbackProfile getProfile(Player player) {
        if (SprintKBPlugin.getInstance() != null && SprintKBPlugin.getInstance().getProfileManager() != null) {
            return SprintKBPlugin.getInstance().getProfileManager().getProfile(player);
        }
        return null;
    }
}
