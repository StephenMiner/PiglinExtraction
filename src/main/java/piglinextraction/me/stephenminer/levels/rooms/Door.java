package piglinextraction.me.stephenminer.levels.rooms;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.builders.HordeBuilder;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Door {
    protected final HashMap<Location, BlockData> dataStorage;
    protected final HashMap<Location, Material> matStorage;
    protected List<List<Location>> layers;
    protected final PiglinExtraction plugin;
    protected final Room room;
    protected final String name;

    protected Location loc1;
    protected Location loc2;
    protected List<String> triggers;
    protected boolean spaces;

    private boolean jammable;
    protected boolean open;

    public Door(PiglinExtraction plugin, String name, Room room, Location loc1, Location loc2){
        this.plugin = plugin;
        this.name = name;
        dataStorage = new HashMap<>();
        matStorage = new HashMap<>();
        layers = new ArrayList<>();
        triggers = new ArrayList<>();
        this.room = room;
        this.loc1 = loc1;
        this.loc2 = loc2;
        spaces = true;
        loadLocations();
        loadTriggers();
        Bukkit.broadcastMessage("Door object");
    }

    public void loadTriggers(){
        triggers = plugin.roomsFile.getConfig().getStringList("rooms." + room.getId() + ".doors." + name + ".triggers");
    }

    public void loadLocations(){
        List<List<Location>> layers = new ArrayList<>();
        World world = loc1.getWorld();
        int minx = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int miny = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minz = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxx = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxy = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxz = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        for (int y = miny; y <= maxy; y++){
            List<Location> layer = new ArrayList<>();
            for (int x = minx; x <= maxx; x++){
                for (int z = minz; z <= maxz; z++){
                    Location loc = new Location(world, x, y, z);
                    layer.add(loc);
                    dataStorage.put(loc, loc.getBlock().getBlockData());
                    matStorage.put(loc, loc.getBlock().getType());
                }
            }
            layers.add(layer);
        }
        this.layers = layers;
    }

    public void resetDoor(){
        for (Location loc : matStorage.keySet()){
            Block block = loc.getBlock();
            block.setType(matStorage.get(loc));
            block.setBlockData(dataStorage.get(loc));
        }
    }


    public void close(){
        new BukkitRunnable(){
            final int maxy = Math.max(loc1.getBlockY(), loc2.getBlockY());
            final int miny = Math.min(loc1.getBlockY(), loc2.getBlockY());
            int offset = 1;
            boolean done = false;
            @Override
            public void run(){
                if (open){
                    this.cancel();
                    return;
                }
                for (int i = 0; i < layers.size(); i++){
                    List<Location> blocks = layers.get(i);

                    for (Location loc : blocks){
                        int toAdd = maxy - loc.getBlockY() - offset + i;
                        if (toAdd == 0) done = true;
                        if (loc.getBlockY() + toAdd <= maxy) {
                            Location temp = loc.clone().add(0, toAdd, 0);
                            temp.getBlock().setType(matStorage.get(loc));
                            temp.getBlock().setBlockData(dataStorage.get(loc));
                        }
                    }

                }
                if (done){
                    this.cancel();
                    Bukkit.broadcastMessage("Closed");
                    return;
                }
                offset++;
            }
        }.runTaskTimer(plugin, 1, 20);
    }

    public void open(){
        final int maxy = Math.max(loc1.getBlockY(), loc2.getBlockY());
        final int miny = Math.min(loc1.getBlockY(), loc2.getBlockY());
        new BukkitRunnable() {
            int y = 1;
            @Override
            public void run() {
                if (!open){
                    this.cancel();
                    return;
                }
                for (int i = 0; i < layers.size(); i++) {
                    List<Location> blocks = layers.get(i);
                    for (Location loc : blocks) {
                        if (i == 0) loc.clone().add(0,y-1,0).getBlock().setType(Material.AIR);
                        if (loc.getY() + y <= maxy) {
                            Location temp = loc.clone().add(0, y, 0);
                            temp.getBlock().setType(matStorage.get(loc));
                            temp.getBlock().setBlockData(dataStorage.get(loc));
                            particleEffect(temp.getBlock());
                        }
                    }
                    if (i == 0 && loc1.getBlockY() + y >= maxy){
                        Bukkit.broadcastMessage("done");
                        this.cancel();
                        return;
                    }
                }
                y++;
            }
        }.runTaskTimer(plugin, 1, 60);
    }

    public List<String> triggers(){
        return triggers;
    }

    public void particleEffect(Block block){
        World world = block.getWorld();
        BoundingBox box = block.getBoundingBox();
        for (double x = box.getMinX(); x <= box.getMaxX(); x+=0.2){
            for (double y = box.getMinY(); y <= box.getMaxY(); y+=0.2){
                for (double z = box.getMinZ(); z <= box.getMaxZ(); z+=0.2){
                    Location loc = new Location(world, x, y, z);
                    world.spawnParticle(Particle.REDSTONE, loc, 0,  new Particle.DustOptions(Color.WHITE, 1));
                }
            }
        }
    }

    public void save(){
        String path = "rooms." + room.getId() + ".doors." + name;
        plugin.roomsFile.getConfig().set(path + ".loc1", plugin.fromBlockLoc(loc1));
        plugin.roomsFile.getConfig().set(path + ".loc2", plugin.fromBlockLoc(loc2));
        plugin.roomsFile.getConfig().set(path + ".triggers", triggers);
        plugin.roomsFile.getConfig().set(path + ".do-spaces", spaces);

        plugin.roomsFile.saveConfig();
    }


    public boolean doSpaces(){ return spaces; }
    //defines whether name's underscore's will get replaced by spaces when displayed
    public void setDoSpaces(boolean spaces){ this.spaces = spaces; }

    public String getName(){
        if (spaces){
            return name.replace('_', ' ');
        }else return name;
    }
    public String getId(){ return name; }


    public boolean isOpen(){
        return open;
    }
    public boolean isJammable(){ return jammable; }
    public void setOpen(boolean open){
        this.open = open;
    }
    public void setJammable(boolean jammable){
        this.jammable = jammable;
    }

    public Room getRoom(){ return room; }


    public static Door fromString(PiglinExtraction plugin, String roomid, String id){
        if (plugin.roomsFile.getConfig().contains("rooms." + roomid + ".doors." + id)){
            if (Room.BY_IDS.containsKey(roomid)){
                Room room = Room.BY_IDS.get(roomid);
                String path = "rooms." + roomid + ".doors." + id;
                Location loc1 = plugin.fromString(plugin.roomsFile.getConfig().getString(path + ".loc1"));
                Location loc2 = plugin.fromString(plugin.roomsFile.getConfig().getString(path + ".loc2"));
                int jamTime = plugin.roomsFile.getConfig().getInt(path + ".jam-time");
                boolean spaces = plugin.roomsFile.getConfig().getBoolean(path + ".do-spaces");
                if (jamTime > 0){
                    String hordeId = plugin.roomsFile.getConfig().getString(path + ".horde");
                    Horde horde = new HordeBuilder(hordeId).build();
                    horde.setLevel(room.getLevel());
                    JammedDoor door = new JammedDoor(plugin, id, room, loc1, loc2, jamTime, horde);
                    door.setDoSpaces(spaces);
                    door.save();
                    door.resetDoor();
                    return door;
                }
                Door door = new Door(plugin, id, room ,loc1, loc2);
                door.setDoSpaces(spaces);
                door.save();
                door.resetDoor();
                return door;
            }
        }
        return null;
    }



}
