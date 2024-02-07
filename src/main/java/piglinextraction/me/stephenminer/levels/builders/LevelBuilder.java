package piglinextraction.me.stephenminer.levels.builders;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.objectives.Objective;
import piglinextraction.me.stephenminer.levels.objectives.RuneObj;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LevelBuilder {
    private final String id;
    private final PiglinExtraction plugin;
    private Location spawn,lobby;
    private String name;
    private Material mat;

    private Level level;
    public LevelBuilder(String id){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.id = id;
    }


    private boolean loadBasicLevel(){
        if (plugin.levelsFile.getConfig().contains("levels." + id)){
            String base = "levels." + id;
            if (plugin.levelsFile.getConfig().contains(base + ".spawn"))
                spawn = plugin.fromString(plugin.levelsFile.getConfig().getString(base + ".spawn"));
            if (plugin.levelsFile.getConfig().contains(base + ".lobby"))
                lobby = plugin.fromString(plugin.levelsFile.getConfig().getString(base + ".lobby"));
            String name = plugin.levelsFile.getConfig().getString(base + ".name");
            Material mat = Material.DIRT;
            if (plugin.levelsFile.getConfig().contains(base + ".mat"))
                mat = Material.matchMaterial(plugin.levelsFile.getConfig().getString(base + ".mat"));
            Level level = new Level(plugin,id,name,mat,spawn, null);
            if (lobby != null) level.setLobby(lobby);
            return true;
        }
        return false;
    }


    private boolean loadRooms(){}





    public static Level fromString(PiglinExtraction plugin, String lvl){
        if (plugin.levelsFile.getConfig().contains("levels." + lvl)){
            String base = "levels." + lvl;
            Location spawn = null;
            Location lobby = null;
            if (plugin.levelsFile.getConfig().contains(base + ".spawn"))
                spawn = plugin.fromString(plugin.levelsFile.getConfig().getString(base + ".spawn"));
            if (plugin.levelsFile.getConfig().contains(base + ".lobby"))
                lobby = plugin.fromString(plugin.levelsFile.getConfig().getString(base + ".lobby"));
            String name = plugin.levelsFile.getConfig().getString(base + ".name");
            Material mat = Material.DIRT;
            if (plugin.levelsFile.getConfig().contains(base + ".mat"))
                mat = Material.matchMaterial(plugin.levelsFile.getConfig().getString(base + ".mat"));
            Level level = new Level(plugin,lvl,name,mat,spawn,null);
            if (plugin.levelsFile.getConfig().contains("levels." + lvl + ".rooms")) {
                List<String> roomNames = plugin.levelsFile.getConfig().getStringList(base + ".rooms");
                List<Room> rooms = new ArrayList<>();
                for (String roomName : roomNames) {
                    try{
                        level.addRoom(Room.fromString(plugin, level, roomName));
                    }catch (Exception e){
                        plugin.getLogger().log(java.util.logging.Level.WARNING, "Error loading room " + roomName);
                    }
                }

            }
            if (lobby != null) level.setLobby(lobby);
            if (plugin.levelsFile.getConfig().contains("levels." + lvl + ".objs")){
                Set<String> objectiveIds = plugin.levelsFile.getConfig().getConfigurationSection("levels." + lvl + ".objs").getKeys(false);
                for (String objId : objectiveIds){
                    Objective obj = switch (objId){
                        case "RUNE_COLLECTION" -> new RuneObj(plugin);
                        default -> null;
                    };
                    if (obj == null) continue;
                    List<String> spawns = plugin.levelsFile.getConfig().getStringList("levels." + lvl + ".objs." + objId + ".spawns");
                    for (String entry : spawns){
                        obj.addSpawn(plugin.fromString(entry));
                    }
                    level.addObjective(obj);
                }
            }
            if (plugin.levelsFile.getConfig().contains("levels." + lvl + ".hordes")){
                Set<String> hordeIds = plugin.levelsFile.getConfig().getConfigurationSection("levels." + lvl + ".hordes").getKeys(false);
                for (String hordeId : hordeIds){
                    Horde horde = Horde.fromId(plugin, hordeId);
                    level.addHorde(horde);
                }
            }
            return level;
        }
        plugin.getLogger().log(java.util.logging.Level.WARNING, "Attempted to load level " + lvl + " but couldn't find entry in config file");
        return null;
    }
}
