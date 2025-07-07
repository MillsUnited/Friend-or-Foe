package com.mills.friendOrFoe;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HeartItem {

    public static ItemStack heart() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Life Token");
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.translateAlternateColorCodes('&', "&c&l(!) &c&oRight click to claim"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
