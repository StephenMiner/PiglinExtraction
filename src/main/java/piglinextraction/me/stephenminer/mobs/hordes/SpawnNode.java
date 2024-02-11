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
    private final Horde horde;


    public SpawnNode(Horde horde, Location loc, int toSpawn,int delay, Class<? extends PiglinEntity>... entities){
        this(horde, loc, toSpawn, delay, Arrays.asList(entities));
    }
    public SpawnNode(Horde horde,Location loc, int toSpawn, int delay,List<Class<? extends PiglinEntity>> entities){
        this.plugin = PiglinExtraction.getPlugin(PiglinExtraction.class);
        this.spawnDelay = delay;
        this.types = entities;
        this.loc = loc.clone().add(0.5,0.5,0.5);
        this.toSpawn = toSpawn;
        this.horde = horde;
    }

    public void spawn(){


        new BukkitRunnable(){
            int index;
            int i = 0;
            @Override
            public void run(){
                if (i > toSpawn){
                    this.cancel();
                    return;
                }
                i++;
                index = ThreadLocalRandom.current().nextInt(types.size());
                Class<? extends PiglinEntity> clazz = types.get(index);
                spawnEntity(clazz);

            }
        }.runTaskTimer(plugin,1,spawnDelay);
    }


    private boolean spawnEntity(Class<? extends PiglinEntity> clazz){
        try {
            PiglinEntity entity = clazz
                    .getConstructor(PiglinExtraction.class,  Location.class)
                    .newInstance(plugin,loc);
            horde.getLevel().getSpawned().put(entity.getMob().getUniqueId(),entity);
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

    public String toString(){
        StringBuilder out = new StringBuilder(plugin.fromLoc(loc) + "/" + toSpawn + "/" + spawnDelay + "/");
        for (Class<? extends PiglinEntity> clazz : types){
            out.append(clazz.getName()).append("/");
        }
        out.deleteCharAt(out.length()-1);
        return out.toString();
    }


}
