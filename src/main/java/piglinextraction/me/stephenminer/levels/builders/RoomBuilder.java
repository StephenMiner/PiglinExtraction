package piglinextraction.me.stephenminer.levels.builders;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Locker;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.rooms.Door;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.levels.spawners.Node;
import piglinextraction.me.stephenminer.levels.spawners.Repeater;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RoomBuilder {
    private final PiglinExtraction plugin;
    private final String id,base;
    private final Level level;
    private Room room;


    public RoomBuilder(String id, Level level){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.id = id;
        this.level = level;
        this.base = "rooms." + id;
    }


    private void loadBasicData(){
        if (!plugin.roomsFile.getConfig().contains(base)){
            plugin.getLogger().warning("Attempted to get room " + id + ", but the room doesn't exist in files!");
            return;
        }
        Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString(base + ".loc1"));
        Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString(base + ".loc2"));
        room = new Room(plugin, id, loc1, loc2, level);
    }

    private void loadNodes(){
        String base = this.base + ".nodes";
        if (!plugin.roomsFile.getConfig().contains(base)) return;

        Set<String> nodeNames = plugin.roomsFile.getConfig().getConfigurationSection(base).getKeys(false);
        List<Node> nodes = new ArrayList<>();
        for (String entry : nodeNames){
            String root = base + "." + entry;
            Location loc = plugin.fromString(entry);
            PiglinType type = PiglinType.valueOf(plugin.roomsFile.getConfig().getString(root + ".entity"));
            int radius = plugin.roomsFile.getConfig().getInt(root + ".radius");
            if (plugin.roomsFile.getConfig().contains(root + ".interval")){
                int interval = plugin.roomsFile.getConfig().getInt(root + ".interval");
                nodes.add(new Repeater(plugin, loc, radius, interval, room, type));
            }else nodes.add(new Node(plugin, loc, radius, room, type));
        }
        room.setNodes(nodes);
    }

    private void loadLockers(){
        if (plugin.roomsFile.getConfig().contains(base + ".lockers")){
            Set<String> lockerNames = plugin.roomsFile.getConfig().getConfigurationSection(base + ".lockers").getKeys(false);
            List<Locker> lockers = new ArrayList<>();
            for (String entry : lockerNames){
                Location loc = plugin.fromString(entry);
                String type = plugin.roomsFile.getConfig().getString(base + ".lockers." + entry + ".type");
                Material mat = Material.matchMaterial(plugin.roomsFile.getConfig().getString(base + ".lockers." + entry + ".mat"));
                BlockData data = Bukkit.createBlockData(plugin.roomsFile.getConfig().getString(base + ".lockers." + entry + ".data"));
                boolean locked = ThreadLocalRandom.current().nextBoolean();//plugin.roomsFile.getConfig().getBoolean("rooms." + name + ".lockers." + entry + ".locked");
                lockers.add(new Locker(plugin, id, type, loc, data, mat, locked));
            }
            room.setLockers(lockers);
        }
    }

    private void loadDoors(){
        if (plugin.roomsFile.getConfig().contains(base + ".doors")){
            Set<String> doorNames = plugin.roomsFile.getConfig().getConfigurationSection(base + ".doors").getKeys(false);
            for (String str : doorNames){
                Door door = Door.fromString(plugin, room.getId(), str);
                door.resetDoor();
                room.addDoor(door);

            }
        }
    }

    public Room build(){
        loadBasicData();
        loadNodes();
        loadLockers();
        loadDoors();
        return room;
    }

    /* Reference method (Original static method)
    public static Room fromString(PiglinExtraction plugin, piglinextraction.me.stephenminer.levels.Level level, String name){
        if (!plugin.roomsFile.getConfig().contains("rooms." + name)){
            plugin.getLogger().warning("Attempted to get room " + name + ", but the room doesn't exist in files!");
            return null;
        }
        Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString("rooms." + name + ".loc1"));
        Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString("rooms." + name + ".loc2"));
        Room room = new Room(plugin, name, loc1, loc2, level);
        if (plugin.roomsFile.getConfig().contains("rooms." + name + ".nodes")){
            //init nodes
            Set<String> nodeNames = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + name + ".nodes").getKeys(false);
            List<Node> nodes = new ArrayList<>();
            for (String entry : nodeNames){
                String base = "rooms." + name + ".nodes." + entry;
                Location loc = plugin.fromString(entry);
                PiglinType type = PiglinType.valueOf(plugin.roomsFile.getConfig().getString(base + ".entity"));
                int radius = plugin.roomsFile.getConfig().getInt(base + ".radius");
                if (plugin.roomsFile.getConfig().contains(base + ".interval")){
                    int interval = plugin.roomsFile.getConfig().getInt(base + ".interval");
                    nodes.add(new Repeater(plugin, loc, radius, interval, room, type));
                }else nodes.add(new Node(plugin, loc, radius, room, type));
            }
            room.setNodes(nodes);
        }
        if (plugin.roomsFile.getConfig().contains("rooms." + name + ".lockers")){
            Set<String> lockerNames = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + name + ".lockers").getKeys(false);
            List<Locker> lockers = new ArrayList<>();
            for (String entry : lockerNames){
                Location loc = plugin.fromString(entry);
                String type = plugin.roomsFile.getConfig().getString("rooms." + name + ".lockers." + entry + ".type");
                Material mat = Material.matchMaterial(plugin.roomsFile.getConfig().getString("rooms." + name + ".lockers." + entry + ".mat"));
                BlockData data = Bukkit.createBlockData(plugin.roomsFile.getConfig().getString("rooms." + name + ".lockers." + entry + ".data"));
                boolean locked = ThreadLocalRandom.current().nextBoolean();//plugin.roomsFile.getConfig().getBoolean("rooms." + name + ".lockers." + entry + ".locked");
                lockers.add(new Locker(plugin, name, type, loc, data, mat, locked));
            }
            room.setLockers(lockers);
        }
        if (plugin.roomsFile.getConfig().contains("rooms." + name + ".doors")){
            Set<String> doorNames = plugin.roomsFile.getConfig().getConfigurationSection("rooms." + name + ".doors").getKeys(false);
            for (String str : doorNames){
                Door door = Door.fromString(plugin, room.getId(), str);
                door.resetDoor();
                room.addDoor(door);

            }
        }
        return room;
    }

     */
}
