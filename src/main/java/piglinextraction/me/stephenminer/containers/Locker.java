package piglinextraction.me.stephenminer.containers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Locker {
    private Location loc;
    private Material mat;
    private BlockData data;
    private byte tier;
    private List<ItemStack[]> loottables;
    private boolean locked;
    private String id;
    private final PiglinExtraction plugin;
    private final String roomId;

    public Locker(PiglinExtraction plugin, String roomId, String type, Location loc, BlockData data, Material mat, boolean locked){
        loottables = new ArrayList<>();
        tier = 1;
        loc.getBlock().setType(mat);
        loc.getBlock().setBlockData(data);
        this.locked = locked;
        this.id = type;
        this.plugin = plugin;
        this.mat = mat;
        this.loc = loc;
        this.roomId = roomId;
        loadTables();
        fillInventory();
    }


    public void loadTables(){
        String path = "lockers." + id + ".loot-tables";
        if (plugin.lockersFile.getConfig().contains(path)){
            for (String entry : plugin.lockersFile.getConfig().getConfigurationSection(path).getKeys(false)){
                int index = Integer.parseInt(entry);
                ItemStack[] items = new ItemStack[27];
                Set<String> section = plugin.lockersFile.getConfig().getConfigurationSection(path + "." + entry).getKeys(false);
                for (String key : section){
                    int pos = Integer.parseInt(key);
                    ItemStack item = plugin.lockersFile.getConfig().getItemStack(path + "." + entry + "." + key);
                    items[pos] = item;
                }
                //ItemStack[] items = plugin.lockersFile.getConfig().getList(path + "." + entry).toArray(new ItemStack[0]);
                loottables.add(index, items);
            }
        }
    }

    public void addLoottable(ItemStack[] table) {
        loottables.add(table);
    }

    public void save(){
        String base = "rooms." + roomId + ".lockers." + plugin.fromBlockLoc(loc);
        plugin.roomsFile.getConfig().set(base + ".type", id);
        plugin.roomsFile.getConfig().set(base + ".mat", mat.name());
        plugin.roomsFile.getConfig().set(base + ".locked", locked);
        plugin.roomsFile.saveConfig();
    }



    public void fillInventory(){
        Block block = loc.getBlock();
        Container container = (Container) block.getState();
        ItemStack[] stacks = loottables.get(ThreadLocalRandom.current().nextInt(loottables.size()));
        for(int i = 0; i < stacks.length; i++){
            ItemStack item = stacks[i];
            if (item != null)
                container.getInventory().setItem(i, item);
        }
        //container.getInventory().addItem(loottables.get(ThreadLocalRandom.current().nextInt(loottables.size())));
    }
    public void clearInventory(){
        Block block = loc.getBlock();
        Container container = (Container) block.getState();
        container.getInventory().clear();
    }
    public void unlock(boolean unlock){ this.locked = !unlock; }
    public boolean isLocked(){ return locked; }
    public Location getLocation(){ return loc; }
}
