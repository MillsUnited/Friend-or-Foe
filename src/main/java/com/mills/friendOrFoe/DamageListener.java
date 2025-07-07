package com.mills.friendOrFoe;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {

    private final PVPManager pvpManager;

    public DamageListener(PVPManager pvpManager) {
        this.pvpManager = pvpManager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            if (!pvpManager.isPvpAllowed()) {
                e.setCancelled(true);
                e.getDamager().sendMessage("§cPvP is currently disabled!");
            }
        }
    }

}
