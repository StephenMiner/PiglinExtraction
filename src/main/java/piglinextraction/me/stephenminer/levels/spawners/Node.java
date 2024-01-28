package piglinextraction.me.stephenminer.levels.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.util.BoundingBox;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.mobs.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Node {
    protected final PiglinExtraction plugin;
    protected final PiglinType toSpawn;
    protected Location base;
    protected int radius;
    protected final Room room;
    protected List<PiglinEntity> entities;

    public Node(PiglinExtraction plugin, Location loc, int radius, Room room,  PiglinType toSpawn){
        entities = new ArrayList<>();
        this.plugin = plugin;
        this.toSpawn = toSpawn;
        this.room = room;
        this.base = loc;
        this.radius = radius;
        Bukkit.broadcastMessage("Node object");
    }

    public void spawn(){
        spawnEntity();
    }


    protected void spawnEntity(){
        PiglinEntity entity = switch (toSpawn) {
            case GRUNT -> new PiglinGrunt(plugin, getRandomLoc());

            case GUARD -> new PiglinGuard(plugin, getRandomLoc());

            case NECROMANCER -> new Necromancer(plugin, getRandomLoc());
            default -> null;
        };
        if (entity != null){
            room.getLevel().getSpawned().put(entity.getMob().getUniqueId(),entity);
        }
    }



    public Location getRandomLoc(){
        List<Location> locs = new ArrayList<>();
        World world = base.getWorld();
        for (int x = base.getBlockX() - radius; x <= base.getBlockX() + radius; x++){
            for (int y = base.getBlockY() - radius; y <= base.getBlockY() + radius; y++){
                for (int z = base.getBlockZ() - radius; z <= base.getBlockZ() + radius; z++){
                    Location loc = new Location(world, x, y, z);
                    Location loc1 = loc.clone().add(0,1,0);
                    Location loc2 = loc.clone().add(0,2,0);
                    if (!loc.getBlock().getType().isAir() &&
                            loc1.getBlock().getType().isAir() && loc2.getBlock().getType().isAir()){
                        BoundingBox box = BoundingBox.of(room.getCorner1().clone().add(0.5,0.5,0.5), room.getCorner2().clone().add(0.5,0.5,0.5));
                        if (box.overlaps(loc.getBlock().getBoundingBox()) && box.overlaps(loc1.getBlock().getBoundingBox()) && box.overlaps(loc2.getBlock().getBoundingBox()))
                            locs.add(loc.add(0.5, 0, 0.5));
                    }
                }
            }
        }
        if (locs.size() > 0)
            return locs.get(ThreadLocalRandom.current().nextInt(locs.size()));
        else return base.clone().add(0.5,1,0.5);
    }

    public void save(){
        String base = "rooms." + room.getId() + ".nodes." + plugin.fromBlockLoc(this.base);
        plugin.roomsFile.getConfig().set(base + ".entity", toSpawn.name());
        plugin.roomsFile.getConfig().set(base + ".radius", radius);
        plugin.roomsFile.saveConfig();
    }

    public List<PiglinEntity> getEntities(){ return entities; }
    public Location getBase(){ return base; }





}
