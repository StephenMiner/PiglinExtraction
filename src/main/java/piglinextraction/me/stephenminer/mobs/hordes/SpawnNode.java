package piglinextraction.me.stephenminer.mobs.hordes;

import org.bukkit.Location;
import org.bukkit.entity.Pig;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * A componant for the horde class specifically
 */
public class SpawnNode {
    private final PiglinExtraction plugin;
    private final Location loc;
    private final int toSpawn;
    private final int spawnDelay;
    private final List<Class<? extends PiglinEntity>> types;
    private final Set<PiglinEntity> spawned;


    public SpawnNode(Location loc, int toSpawn,int delay, Class<? extends PiglinEntity>... entities){
        this(loc, toSpawn, delay, Arrays.asList(entities));
    }
    public SpawnNode(Location loc, int toSpawn, int delay,List<Class<? extends PiglinEntity>> entities){
        this.plugin = PiglinExtraction.getPlugin(PiglinExtraction.class);
        this.spawnDelay = delay;
        this.types = entities;
        this.loc = loc;
        this.toSpawn = toSpawn;
        this.spawned = new HashSet<>();
    }

    public void spawn(){

        int i = 0;
        new BukkitRunnable(){
            int index;
            @Override
            public void run(){
                if (i > toSpawn){
                    this.cancel();
                    return;
                }
                index = ThreadLocalRandom.current().nextInt(types.size());
                Class<? extends PiglinEntity> clazz = types.get(index);
                spawnEntity(clazz);

            }
        }.runTaskTimer(plugin,1,spawnDelay);
    }


    private boolean spawnEntity(Class<? extends PiglinEntity> clazz){
        try {
            PiglinType type = fromClass(clazz);
            if (type == null) return false;
            PiglinEntity entity = clazz
                    .getConstructor(PiglinExtraction.class, PiglinType.class, Location.class)
                    .newInstance(plugin, type, loc);
            spawned.add(entity);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private PiglinType fromClass(Class<? extends PiglinEntity> clazz){
         if (clazz.equals(PiglinGrunt.class)) return PiglinType.GRUNT;
         else if (clazz.equals(Necromancer.class)) return PiglinType.NECROMANCER;
         else if (clazz.equals(PiglinGuard.class)) return PiglinType.GUARD;
         else return null;
    }


    public Location getLoc(){ return loc; }
    public int getTospawn(){ return toSpawn; }
    public List<Class<? extends PiglinEntity>> getTypes(){ return types; }
    public Set<PiglinEntity> getSpawned(){ return spawned; }

    public String toString(){
        StringBuilder out = new StringBuilder(plugin.fromLoc(loc) + "/" + toSpawn + "/" + spawnDelay + "/");
        for (Class<? extends PiglinEntity> clazz : types){
            out.append(clazz.getName()).append("/");
        }
        out.deleteCharAt(out.length());
        return out.toString();
    }

    public static SpawnNode parseNode(String str){
        PiglinExtraction plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        String[] split = str.split("/");
        Location loc = plugin.fromString(split[0]);
        int toSpawn = Integer.parseInt(split[1]);
        int spawnDelay = Integer.parseInt(split[2]);
        List<Class<? extends PiglinEntity>> types = new ArrayList<>();
        for (int i = 3; i < split.length; i++){
            try {
                Class<? extends PiglinEntity> clazz = (Class<? extends PiglinEntity>) Class.forName(split[i]);
                types.add(clazz);
            }catch (ClassNotFoundException e){
                e.printStackTrace();
                plugin.getLogger().log(Level.WARNING,split[i] + " isnt a valid PiglinEntity class name");
            }
        }
        return new SpawnNode(loc,toSpawn,spawnDelay,types);
    }
}
