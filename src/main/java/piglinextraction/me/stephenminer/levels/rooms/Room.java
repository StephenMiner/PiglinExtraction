package piglinextraction.me.stephenminer.levels.rooms;

import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Locker;
import piglinextraction.me.stephenminer.events.custom.RoomEnterEvent;
import piglinextraction.me.stephenminer.levels.spawners.Node;
import piglinextraction.me.stephenminer.levels.spawners.Repeater;
import piglinextraction.me.stephenminer.mobs.PiglinType;
import piglinextraction.me.stephenminer.mobs.boss.encounters.Encounter;
import piglinextraction.me.stephenminer.mobs.boss.encounters.RoomEncounter;

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
        this.corner1 = corner1.getBlock().getLocation().clone().add(0.5,0.5,0.5);
        this.corner2 = corner2.getBlock().getLocation().clone().add(0.5,0.5,0.5);;
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
        this.corner1 = corner1.getBlock().getLocation().clone().add(0.5,0.5,0.5);;
        this.corner2 = corner2.getBlock().getLocation().clone().add(0.5,0.5,0.5);;
        this.destruction = destruction;
        this.id = id;
        BY_IDS.put(id, this);
        this.level = level;
        Bukkit.broadcastMessage("Room object 1");
    }


    public void listenEncounters(boolean enter){
        level.getEncounters().stream()
                .filter(encounter -> encounter instanceof RoomEncounter roomEncounter && roomEncounter.getRoom().equals(this))
                .forEach(encounter -> {
                    RoomEncounter e = (RoomEncounter) encounter;
                    if (enter){
                        if (e.getFlag() == RoomEncounter.Flag.ON_ENTER) e.trigger();
                    }
                });
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
        BoundingBox box = BoundingBox.of(corner1,corner2);
        Collection<Entity> entities = corner1.getWorld().getNearbyEntities(box);
        for (Entity entity : entities){
            if (entity instanceof Player) continue;
            if (entity instanceof LivingEntity living){
                living.setHealth(0);
                living.remove();
            }
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
        Room room = this;
        mark = false;
        inRoom = new HashSet<>();
        Set<UUID> old = new HashSet<>();
        World world = corner1.getWorld();
        BoundingBox box = BoundingBox.of(corner1,corner2);
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!level.isStarted() || mark){
                    this.cancel();
                    return;
                }
                Collection<Entity> near = world.getNearbyEntities(box);
                old.clear();
                old.addAll(inRoom);
                inRoom.clear();
                for (Entity e : near){
                    if (e instanceof Player p) {
                        if (!old.contains(p.getUniqueId()))
                            Bukkit.getServer().getPluginManager().callEvent(new RoomEnterEvent(room, p));
                        inRoom.add(p.getUniqueId());
                    }
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

    private BoundingBox box;
    public boolean isInRoom(Location loc){
        if (box == null) box = BoundingBox.of(corner1,corner2);
        return (box.overlaps(BoundingBox.of(loc.toVector(),loc.toVector())));
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

    public Set<UUID> getInRoom() {return inRoom; }

    public void setKill(boolean kill){
        this.kill = kill;
    }
    public boolean kill(){ return kill; }



}
