package piglinextraction.me.stephenminer.mobs.hordes;

import org.bukkit.Location;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashSet;

/**
 * A componant for the horde class specifically
 */
public class SpawnNode {
    private final PiglinExtraction plugin;
    private final Location loc;
    private final int toSpawn;
    private final List<Class<? extends PiglinEntity>> types;
    private final Set<PiglinEntity> spawned;


    public SpawnNode(Location loc, int toSpawn, Class<? extends PiglinEntity>... entities){
        this(loc, toSpawn, Arrays.asList(entities));
    }
    public SpawnNode(Location loc, int toSpawn, List<Class<? extends PiglinEntity>> entities){
        this.plugin = PiglinExtraction.getPlugin(PiglinExtraction.class);
        this.types = entities;
        this.loc = loc;
        this.toSpawn = toSpawn;
        this.spawned = new HashSet<>();
    }

    public void spawn(){
        int index;
        for (int i = 0; i < toSpawn; i++){
            index = ThreadLocalRandom.current().nextInt(types.size());
            Class<? extends PiglinEntity> clazz = types.get(index);
            spawnEntity(clazz);
        }
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
}
