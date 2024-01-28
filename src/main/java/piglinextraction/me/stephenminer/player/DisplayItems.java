package piglinextraction.me.stephenminer.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DisplayItems {



    public ItemStack displayLongRifle(){
        ItemStack item = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Dwarven Longrifle");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Uses mana and dwarf engineering");
        lore.add(ChatColor.ITALIC + "to launch arrows at high speeds!");
        lore.add(ChatColor.YELLOW + "left-click to reload");
        lore.add(ChatColor.YELLOW + "right-click to shoot");
        lore.add(ChatColor.YELLOW + "shift to zoom");
        lore.add(ChatColor.AQUA + "Click to select item");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack displayRepeater(){
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Repeater Crossbow");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A rapid fire crossbow");
        lore.add(ChatColor.ITALIC + "left-click to reload");
        lore.add(ChatColor.ITALIC + "right-click to shoot");
        lore.add(ChatColor.AQUA + "Click to select item");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack displayShortSword(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Short-Sword");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A Fast Paced Weapon");
        lore.add(ChatColor.ITALIC + "Fast attack speed");
        lore.add(ChatColor.ITALIC + "Bonus headshot dmg");
        lore.add(ChatColor.AQUA + "Click to select item");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack displaySpear(){
        ItemStack item = new ItemStack(Material.TRIDENT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Spear");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A tried and true weapon");
        lore.add(ChatColor.ITALIC + "High range, good damage");
        lore.add(ChatColor.AQUA + "Click to select item");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack displayWarhammer(){
        ItemStack item = new ItemStack(Material.IRON_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Dwarven Warhammer");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A classic sweeping weapon");
        lore.add(ChatColor.ITALIC + "Good damage, high charge time");
        lore.add(ChatColor.AQUA + "Click to select item");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack selectorItem(){
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Loadout Selector");
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.ITALIC + "Click to edit your loadout!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

}
