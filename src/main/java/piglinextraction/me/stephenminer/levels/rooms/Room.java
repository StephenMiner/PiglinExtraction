package piglinextraction.me.stephenminer.levels.rooms;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Locker;
import piglinextraction.me.stephenminer.levels.spawners.Node;
import piglinextraction.me.stephenminer.levels.spawners.Repeater;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Room {
    public static HashMap<String, Room> BY_IDS = new HashMap<>();

    protected List<Location> storedLocs;
    protected List<Locker> lockers;
    protected List<Node> nodes;
    protected List<Door> doors;
    protected final PiglinExtraction plugin;
    protected final Location corner1;
    protected final Location corner2;
    protected final String id;
    protected boolean destruction;
    protected boolean outline;
    protected boolean editmode;
    protected boolean mark;
    protected boolean kill;
    protected int oldAgitation;
    protected final piglinextraction.me.stephenminer.levels.Level level;
    protected List<Mob> fromNodes;
    public Room(PiglinExtraction plugin, String id, Location corner1, Location corner2, piglinextraction.me.stephenminer.levels.Level level){
        nodes = new ArrayList<>();
        lockers = new ArrayList<>();
        fromNodes = new ArrayList<>();
        doors = new ArrayList<>();
        this.level = level;
        this.plugin = plugin;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.id = id;
        destruction = false;
        BY_IDS.put(id, this);
        loadLocs();
        Bukkit.broadcastMessage("Room object");
    }
    public Room(PiglinExtraction plugin, String id, Location corner1, Location corner2, boolean destruction, piglinextraction.me.stephenminer.levels.Level level){
        nodes = new ArrayList<>();
        lockers = new ArrayList<>();
        this.plugin = plugin;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.destruction = destruction;
        this.id = id;
        BY_IDS.put(id, this);
        this.level = level;
        Bukkit.broadcastMessage("Room object 1");
    }



    public boolean protect(Location loc){
        if (destruction) return false;
        BoundingBox box = BoundingBox.of(corner1, corner2);
        return box.overlaps(loc.getBlock().getBoundingBox());
    }
    public void load(){
        for (Node node : nodes){
            node.spawn();
        }
        for (Locker locker : lockers){
            locker.fillInventory();
        }
        updateInRoom();
    }
    public void unload(){
        Bukkit.broadcastMessage(id);
        mark = true;
        for (Locker locker : lockers){
            locker.clearInventory();
        }
    }

    public void reload(){
        unload();
        load();
    }

    public void save(){
        String path = "rooms." + id;
        plugin.roomsFile.getConfig().set(path + ".loc1", plugin.fromLoc(corner1));
        plugin.roomsFile.getConfig().set(path + ".loc2", plugin.fromLoc(corner2));
        for (Node node : nodes){
            node.save();
        }
        for (Locker locker : lockers){
            locker.save();
        }
        plugin.roomsFile.saveConfig();
    }

    public void addNode(Node node){
        nodes.add(node);
    }
    public void addNodes(List<Node> add){
        nodes.addAll(add);
    }
    public void setNodes(List<Node> nodes){
        this.nodes = nodes;
    }
    public void setLockers(List<Locker> lockers){ this.lockers = lockers;}
    public void addLocker(Locker locker){ this.lockers.add(locker); }
    public void addDoor(Door door){
         doors.add(door);
    }
    public void setDoors(List<Door> doors){
        this.doors = doors;
    }

    public void removeNode(Node node){
        nodes.remove(node);
        plugin.roomsFile.getConfig().set("rooms." + id + ".nodes." + plugin.fromBlockLoc(node.getBase()), null);
        plugin.roomsFile.saveConfig();
    }
    public void removeNode(Location loc){
        for (int i = 0; i < nodes.size(); i++){
            Node node = nodes.get(i);
            if (plugin.fromBlockLoc(node.getBase()).equalsIgnoreCase(plugin.fromBlockLoc(loc))){
                nodes.remove(node);
                plugin.roomsFile.getConfig().set("rooms." + id + ".nodes." + plugin.fromBlockLoc(loc), null);
                plugin.roomsFile.saveConfig();
            }
        }
    }


    public void loadLocs(){
        storedLocs = new ArrayList<>();
        World world = corner1.getWorld();
        for (int x = minX(); x <= maxX(); x++){
            for (int y = minY(); y <= minY(); y++){
                for (int z = minZ(); y <= minZ(); z++){
                    storedLocs.add(new Location(world, x, y, z));
                }
            }
        }
    }

    private Set<UUID> inRoom;

    private void updateInRoom(){
        mark = false;
        inRoom = new HashSet<>();
        World world = corner1.getWorld();
        BoundingBox box = BoundingBox.of(corner1.getBlock().getLocation().clone().add(0.5,0.5,0.5), corner2.getBlock().getLocation().add(0.5,0.5,0.5));
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!level.isStarted() || mark){
                    this.cancel();
                    return;
                }
                Collection<Entity> near = world.getNearbyEntities(box);
                inRoom.clear();
                for (Entity e : near){
                    if (e instanceof Player p) inRoom.add(p.getUniqueId());
                }
            }
        }.runTaskTimer(plugin,1,10);
    }

    public void getOutline(){
        World world = corner1.getWorld();
        Vector fp = corner1.toVector();
        Vector sp = corner2.toVector();
        Vector max = org.bukkit.util.Vector.getMaximum(fp, sp);
        Vector min = Vector.getMinimum(fp, sp);
        Set<Location> locSet = new HashSet<>();
        for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
            for (int x = min.getBlockX() - 1; x <= max.getBlockX() + 1; x++) {
                locSet.add(new Location(world, x, y, min.getBlockZ() - 1));
                locSet.add(new Location(world, x, y, max.getBlockZ() + 1));
            }
            for (int z = min.getBlockZ() - 1; z <= max.getBlockZ() + 1; z++) {
                locSet.add(new Location(world,min.getBlockX() - 1, y, z));
                locSet.add(new Location(world,max.getBlockX() + 1, y, z));
            }
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!outline) this.cancel();
                for (Location loc : locSet){
                    Location l = loc.clone().add(0.5,0.5,0.5);
                    world.spawnParticle(Particle.VILLAGER_HAPPY, l, 0);
                }
            }
        }.runTaskTimer(plugin, 1, 5);

    }


    private int minX(){ return Math.min(corner1.getBlockX(), corner2.getBlockX()); }
    private int minY(){ return Math.min(corner1.getBlockY(), corner2.getBlockY()); }
    private int minZ(){ return Math.min(corner1.getBlockZ(), corner2.getBlockZ()); }

    private int maxX(){ return Math.max(corner1.getBlockX(), corner2.getBlockX()); }
    private int maxY(){ return Math.max(corner1.getBlockY(), corner2.getBlockY()); }
    private int maxZ(){ return Math.max(corner1.getBlockZ(), corner2.getBlockZ()); }

    public List<Location> getStoredLocs(){
        return storedLocs;
    }

    public piglinextraction.me.stephenminer.levels.Level getLevel(){ return level; }
    public Location getCorner1(){ return corner1; }
    public Location getCorner2(){ return corner2; }
    public boolean canDestroy(){ return destruction; }
    public void setDestroy(boolean destruction){ this.destruction = destruction; }
    public String getId(){ return id; }
    public boolean showingOutline(){ return outline; }
    public void setShowingOutline(boolean outline){ this.outline = outline; }

    public void setEditMode(boolean editmode){this.editmode = editmode; }
    public boolean editModeOn(){ return editmode;}

    public List<Node> getNodes(){ return nodes; }
    public List<Locker> getLockers(){ return lockers; }
    public List<Mob> getFromNodes(){ return fromNodes; }
    public List<Door> getDoors(){ return doors; }

    public void setKill(boolean kill){
        this.kill = kill;
    }
    public boolean kill(){ return kill; }


    public static Room fromString(PiglinExtraction plugin, piglinextraction.me.stephenminer.levels.Level level, String name){
        if (!plugin.roomsFile.getConfig().contains("rooms." + name)){
            plugin.getLogger().log(Level.WARNING, "Attempted to get room " + name + ", but the room doesn't exist in files!");
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
}