package piglinextraction.me.stephenminer.containers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class LockerGui {
    private Inventory current;
    private String currentTitle;
    private int currentNum;

    private PiglinExtraction plugin;
    private String id;
    public LockerGui(PiglinExtraction plugin, String id){
        this.plugin = plugin;
        this.id = id;
    }

    public Inventory lootTable(int num){
        currentTitle = id + " loot table #" + num;
        currentNum = num;
        Inventory inv = Bukkit.createInventory(null,36, currentTitle);
        current = inv;
        for (int i = 27; i < 36; i++){
            inv.setItem(i, filler());
        }
        inv.setItem(31, close());
        if (plugin.lockersFile.getConfig().contains("lockers." + id + ".loot-tables." + num))
            loadLootTable(num);

        return inv;
    }

    private ItemStack filler(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack close(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close & Save");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "simple as");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void loadLootTable(int num){
        Set<String> section = plugin.lockersFile.getConfig().getConfigurationSection("lockers." + id + ".loot-tables." + num).getKeys(false);
        for (String key : section){
            int index = Integer.parseInt(key);
            Bukkit.broadcastMessage(index + "");
            ItemStack item = plugin.lockersFile.getConfig().getItemStack("lockers." + id + ".loot-tables." + num + "." + key);
            current.setItem(index, item);

        }
        try {

        }catch (Exception e){
            plugin.getLogger().log(Level.WARNING, "Something went wrong loading items list for locker " + id + " #" + num);
            plugin.getLogger().log(Level.INFO, e.getMessage());
        }
    }

    public void save(){
        for (int i = 0; i < 27; i++){
           ItemStack item = current.getItem(i);
           if (item != null)
            plugin.lockersFile.getConfig().set("lockers." + id + ".loot-tables." + currentNum + "." + i, item);
        }
        plugin.lockersFile.saveConfig();
    }

    public Inventory getCurrent(){ return current; }
    public String getCurrentTitle(){ return currentTitle; }
    public int getCurrentNum(){ return currentNum; }

}
