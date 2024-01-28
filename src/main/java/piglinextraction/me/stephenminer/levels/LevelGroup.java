package piglinextraction.me.stephenminer.levels;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;

public class LevelGroup {
    public static List<LevelGroup> levelGroups = new ArrayList<>();
    private final PiglinExtraction plugin;
    private List<String> levels;
    private Material icon;
    private String name;
    private String displayName;

    public LevelGroup(PiglinExtraction plugin, String name){
        levels = new ArrayList<>();
        this.plugin = plugin;
        this.name = name;
        icon = Material.ACACIA_SIGN;
        displayName = name.replace('_', ' ');
        levelGroups.add(this);
    }


    public void addLevel(String levelId){
        levels.add(levelId);
    }
    public void removeLevel(String levelId){
        levels.remove(levelId);
    }
    public Level getLevel(String id){
        return Level.fromId(id);
    }
    public void save(){
        plugin.groupsFile.getConfig().set("groups." + name + ".levels", levels);
        plugin.groupsFile.getConfig().set("groups." + name + ".icon", icon.name());
        plugin.groupsFile.saveConfig();
    }
    //returns raw name id value
    public String getName(){ return name; }
    //returns name formatted with spaces
    public String getDisplayName(){ return displayName; }

    public List<String> levelNames(){ return levels; }
    public void delete(){
        plugin.groupsFile.getConfig().set("groups." + name, null);
        plugin.groupsFile.saveConfig();
        LevelGroup.levelGroups.remove(this);
    }
    public void setName(String name){
        plugin.groupsFile.getConfig().set("groups." + name, null);
        plugin.groupsFile.saveConfig();
        this.name = name;
        this.displayName = name.replace('_', ' ');
    }
    public Material getIcon(){
        return icon;
    }
    public void setIcon(Material mat){
        this.icon = mat;
    }

    public ItemStack asItem(){
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.BOLD + "" + ChatColor.YELLOW + displayName);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "Click to see lobbies");
        lore.add(ChatColor.BLACK + name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static LevelGroup fromString(PiglinExtraction plugin, String name){
        if (plugin.groupsFile.getConfig().contains("groups." + name)){
            Material mat = Material.matchMaterial(plugin.groupsFile.getConfig().getString("groups." + name + ".icon"));
            LevelGroup levelGroup = new LevelGroup(plugin, name);
            levelGroup.setIcon(mat);
            List<String> lvls = plugin.groupsFile.getConfig().getStringList("groups." + name + ".levels");
            for (String entry : lvls){
                levelGroup.addLevel(entry);
            }
            return levelGroup;
        }
        return null;
    }


}
