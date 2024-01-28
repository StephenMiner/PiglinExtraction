package piglinextraction.me.stephenminer.weapons;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;

public class Flashlight {
    private final PiglinExtraction plugin;
    private final Player owner;
    private final int range;
    private final int lightDegrade;
    private final int baseLight;
    private boolean toggled = false;

    public Flashlight(PiglinExtraction plugin, Player owner, int baseLight, int lightDegrade, int range){
        this.owner = owner;
        this.baseLight = baseLight;
        this.lightDegrade = lightDegrade;
        this.range = range;
        this.plugin = plugin;
        owner.getInventory().setItem(1,flashlight());
    }

    private Location previousLoc;
    private BlockData previousData;
    private Material previousMat;
    public void turnOn(){
        previousLoc = owner.getLocation();
        previousData = owner.getLocation().getBlock().getBlockData();
        previousMat = owner.getLocation().getBlock().getType();
        new BukkitRunnable(){
            @Override
            public void run(){

                previousLoc.getBlock().setType(previousMat);
                previousLoc.getBlock().setBlockData(previousData);

                if (!toggled){
                    this.cancel();
                    return;
                }
                if (owner.getGameMode() == GameMode.SPECTATOR || owner.isDead()){
                    toggled = false;
                    return;
                }
                Location base = owner.getEyeLocation().clone();
                int currentLight = baseLight;
                Vector dir = owner.getLocation().getDirection();
                for (int i = 1; i <= range; i++){
                    base.add(dir);
                    Block block = base.getBlock();
                    if (block.isPassable() && !switches().contains(block.getType())){
                        currentLight = baseLight - i*lightDegrade;
                    }else {
                        base.subtract(dir);
                        break;
                    }
                }
                previousLoc = base;
                previousData = base.getBlock().getBlockData();
                previousMat = base.getBlock().getType();
                Block block = base.getBlock();
                block.setType(Material.LIGHT);
                Light light = (Light) block.getBlockData();
                light.setLevel(currentLight);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private List<Material> switches(){
        List<Material> switches = new ArrayList<>();
        switches.add(Material.BIRCH_BUTTON);
        switches.add(Material.ACACIA_BUTTON);
        switches.add(Material.OAK_BUTTON);
        switches.add(Material.DARK_OAK_BUTTON);
        switches.add(Material.STONE_BUTTON);
        switches.add(Material.POLISHED_BLACKSTONE_BUTTON);
        switches.add(Material.CRIMSON_BUTTON);
        switches.add(Material.WARPED_BUTTON);
        switches.add(Material.JUNGLE_BUTTON);
        switches.add(Material.LEVER);
        return switches;
    }
    public Location getPreviousLoc(){
        return previousLoc;
    }
    public Material getPreviousMat(){
        return previousMat;
    }
    public BlockData getPreviousData(){
        return previousData;
    }
    public Player getOwner(){
        return owner;
    }
    public int getRange(){
        return range;
    }
    public int getLightDegrade(){
        return lightDegrade;
    }
    public int getBaseLight(){
        return baseLight;
    }
    public void toggle(boolean toggled){
        this.toggled = toggled;
    }
    public boolean isToggled(){
        return toggled;
    }


    public ItemStack flashlight(){
        ItemStack item = new ItemStack(Material.SPYGLASS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Dwarven RuneLight");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Uses runes of glowing to produce light!");
        lore.add(ChatColor.YELLOW + "Right-Click to toggle on/off");
        lore.add(ChatColor.BLACK + "flashlight");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
