package piglinextraction.me.stephenminer.containers;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.objectives.ObjectiveType;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.ArrayList;
import java.util.List;

public class Items {
    private final PiglinExtraction plugin;
    public Items(PiglinExtraction plugin){
        this.plugin = plugin;
    }

    public ItemStack roomWand(){
        ItemStack item = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Room Wand");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Left click to set POS 1");
        lore.add(ChatColor.YELLOW + "Right click to set POS 2");
        lore.add(ChatColor.BLACK + "room-wand");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack doorWand(){
        ItemStack item = new ItemStack(Material.IRON_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Door Wand");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Left click to set POS 1");
        lore.add(ChatColor.YELLOW + "Right click to set POS 2");
        lore.add(ChatColor.BLACK + "door-wand");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack breaking(){
        ItemStack item = new ItemStack(Material.FLINT);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Rune of Breaking");
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 2, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Made to break");
        lore.add(ChatColor.ITALIC + "Can destroy locks");
        lore.add(ChatColor.YELLOW + "Right click locked container to use");
        lore.add(ChatColor.BLACK + "breaking");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack lockerItem(String id){
        String path = "lockers." + id;
        if (plugin.lockersFile.getConfig().contains(path)){
            Material mat = Material.matchMaterial(plugin.lockersFile.getConfig().getString(path + ".material"));
            ItemStack item  = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.BLUE + id + " locker");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.BLACK + id);
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }
        return null;
    }

    public ItemStack nodeItem(PiglinType type, int radius){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + type.getName() + " spawner");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "node");
        lore.add(ChatColor.ITALIC + "Spawning: " + type.name());
        lore.add(ChatColor.ITALIC + "Radius: " + radius);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack nodeItem(PiglinType type, int radius, int interval){
        ItemStack item = nodeItem(type, radius);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Repeating " + type.getType() + " spawner");
        List<String> lore = meta.getLore();
        lore.add(ChatColor.ITALIC + "Interval: " + interval);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack nodeItem(ObjectiveType type){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Objective Wand");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Type: " + type.name());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
/*
    public ItemStack levelIcon(Level level){
        ItemStack item = new ItemStack(level.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + level.getName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + level.getId());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

 */
    public ItemStack levelIcon(String levelId){
        Material mat = Material.DIRT;
        try{
            mat = Material.matchMaterial(plugin.levelsFile.getConfig().getString("levels." + levelId + ".mat"));
        }catch (Exception ignored){}
        String name = plugin.levelsFile.getConfig().getString("levels." + levelId + ".name");
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + levelId);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack healingBrew(int uses){
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.PURPLE);
        meta.setDisplayName(ChatColor.BLUE + "Healing Brew (" + uses + " uses)");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A hearty brew that sows your wounds!");
        lore.add(ChatColor.YELLOW + "Uses: " + uses);
        lore.add(ChatColor.YELLOW + "Right-Click: Use brew");
        lore.add(ChatColor.BLACK + "healingbrew");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack quiver(int uses){
        ItemStack item = new ItemStack(Material.LEVER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Quiver (" + uses + " uses)");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "We need a refill over here!");
        lore.add(ChatColor.YELLOW + "Uses: " + uses);
        lore.add(ChatColor.YELLOW + "Right-Click: Use quiver");
        lore.add(ChatColor.BLACK + "quiver");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack filler(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack close(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Exit Menu");
        item.setItemMeta(meta);
        return item;
    }


    public boolean hasLore(ItemStack item, String compareTo){
        if (item.hasItemMeta() && item.getItemMeta().hasLore()){
            List<String> lore = item.getItemMeta().getLore();
            for (String entry : lore){
                entry = ChatColor.stripColor(entry);
                if (entry.equalsIgnoreCase(compareTo)) return true;
            }
        }
        return false;
    }

}
