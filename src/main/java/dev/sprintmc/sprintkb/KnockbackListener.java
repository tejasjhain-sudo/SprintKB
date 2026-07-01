package dev.sprintmc.sprintkb;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Knockback Listener
 *
 * Strategy:
 * 1. On EntityDamageByEntityEvent, record the hit pair (attacker -> victim) and
 *    calculate the custom KB vector, storing it per-victim UUID.
 * 2. On PlayerVelocityEvent (fired by Paper when KB is applied), override the
 *    velocity with our pre-calculated vector.
 *
 * This approach is reliable across all Paper 1.20+ versions without depending
 * on specific Paper-only knockback event method signatures that can differ.
 */
public class KnockbackListener implements Listener {

    private final SprintKBPlugin plugin;
    private final KnockbackEngine engine;
    private final ComboTracker comboTracker;
    private final ConfigManager config;

    // Map victim UUID -> pending custom knockback vector
    private final Map<UUID, Vector> pendingKnockback = new HashMap<>();

    public KnockbackListener(SprintKBPlugin plugin, KnockbackEngine engine,
                              ComboTracker comboTracker, ConfigManager config) {
        this.plugin = plugin;
        this.engine = engine;
        this.comboTracker = comboTracker;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Only PvP melee
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        // Respect cancelled check
        if (plugin.getConfig().getBoolean("compatibility.respect-cancelled-events", true)
                && event.isCancelled()) return;

        // Ignore creative / spectator
        if (plugin.getConfig().getBoolean("compatibility.ignore-creative", true)) {
            switch (victim.getGameMode()) {
                case CREATIVE:
                case SPECTATOR:
                    return;
                default:
                    break;
            }
        }

        // Detect weapon type from attacker's main hand
        String weaponType = detectWeapon(attacker);

        // Register combo hit on victim
        int maxComboTicks = plugin.getConfig().getInt("profiles.default.combo.max-combo-ticks", 18);
        comboTracker.registerHit(victim, maxComboTicks);

        // Pre-calculate the custom KB vector and store it for the upcoming velocity event
        // We use a dummy "original" vector (engine only needs direction from attacker location)
        Vector customKb = engine.calculateKnockback(victim, attacker, new Vector(0, 0.35, 0), weaponType);
        pendingKnockback.put(victim.getUniqueId(), customKb);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Vector customKb = pendingKnockback.remove(uuid);
        if (customKb == null) return; // Not from a PvP hit we're tracking

        // Override the velocity Paper is about to apply
        event.setVelocity(customKb);
    }

    private String detectWeapon(Player player) {
        String typeName = player.getInventory().getItemInMainHand().getType().name();
        if (typeName.contains("SWORD")) return "sword";
        if (typeName.contains("AXE"))   return "axe";
        if (typeName.contains("MACE"))  return "mace";
        return "fist";
    }
}
