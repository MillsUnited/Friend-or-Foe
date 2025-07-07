package com.mills.friendOrFoe;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class LivesListener implements Listener {

    private LivesManager livesManager;

    public LivesListener(LivesManager livesManager) {
        this.livesManager = livesManager;
        startActionBarUpdateTask();
    }

    private void startActionBarUpdateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Main.getInstance().getServer().getOnlinePlayers()) {
                    int lives = livesManager.getPlayerLives(player);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_RED + "Lives"
                            + ChatColor.DARK_GRAY + " » " + ChatColor.RED + lives));
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        int lives = livesManager.loadPlayerLives(player);
        if (lives <= 0) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.RED + "You have no lives remaining, you are in spectator mode.");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        int currentLives = livesManager.getPlayerLives(player);

        if (currentLives > 0) {
            livesManager.setPlayerLives(player, currentLives - 1);
        }

        if (livesManager.getPlayerLives(player) <= 0) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ChatColor.RED + "You have no lives left, you are now in spectator mode.");
        }

        livesManager.savePlayerLives(player);
        player.getWorld().dropItem(player.getLocation(), HeartItem.heart());
    }

    @EventHandler
    public void onPlayerRightClickNameTag(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (itemInHand.getType() == HeartItem.heart().getType()) {
                if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().equals(HeartItem.heart().getItemMeta())) {
                    int currentLives = livesManager.getPlayerLives(player);
                    livesManager.setPlayerLives(player, currentLives + 1);
                    livesManager.savePlayerLives(player);
                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        e.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }
}
