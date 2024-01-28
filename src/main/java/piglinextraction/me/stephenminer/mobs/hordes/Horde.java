package piglinextraction.me.stephenminer.mobs.hordes;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.rooms.Door;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Meant to activate only once / has a defined trigger momment
 * Will spawn a horde of monsters defined by its spawn nodes
 */
public class Horde {
    private final PiglinExtraction plugin;
    private final Set<SpawnNode> nodes;
    private final String id;

    private Trigger trigger;
    private boolean cleared;


    public Horde(String id){
        this.plugin = PiglinExtraction.getPlugin(PiglinExtraction.class);
        this.id = id;
        this.nodes = new HashSet<>();
    }


    public void triggerHorde(){
        for (SpawnNode node : nodes){
            World world = node.getLoc().getWorld();
            world.playSound(node.getLoc(), Sound.EVENT_RAID_HORN,50,1);
            node.spawn();
        }
    }

    /**
     * Will attempt to remove all monsters spawned as a result of this class and its SpawnNodes
     */
    public void clear(){
        for (SpawnNode node : nodes){
            Set<PiglinEntity> spawned = node.getSpawned();
            for (PiglinEntity entity : spawned){
                entity.getMob().setHealth(0);
            }
            node.getSpawned().clear();
        }
    }



    public void save(){
        String path = "hordes." + id;
        for (SpawnNode node : nodes){
            saveNode(node);
        }
        saveTrigger();
        plugin.hordesFile.saveConfig();
    }

    private void saveTrigger(){
        String str = trigger.triggerId() + "/" + trigger.type();
        plugin.hordesFile.getConfig().set("hordes." + id + ".trigger",str);
        plugin.hordesFile.saveConfig();;
    }

    private void saveNode(SpawnNode node){
        String sLoc = plugin.fromBlockLoc(node.getLoc());
        String path = "hordes." + id + ".nodes." + sLoc;
        List<String> sClasses = node.getTypes().stream().map(Class::getName).toList();
        plugin.hordesFile.getConfig().set(path + ".types", sClasses);
        plugin.hordesFile.getConfig().set(path + ".toSpawn", node.getTospawn());
        plugin.hordesFile.saveConfig();
    }






    public String getId(){ return id; }
    public Set<SpawnNode> getNodes(){ return nodes; }
    public Trigger getTrigger(){ return trigger; }

    public void setTrigger(Trigger trigger){ this.trigger = trigger; }


    public boolean addNode(SpawnNode node){
        return nodes.add(node);
    }

    public boolean removeNode(SpawnNode node){
        return nodes.remove(node);
    }
    public boolean isCleared(){ return cleared; }
    public void setCleared(boolean cleared){ this.cleared = cleared; }

    public static Horde fromId(PiglinExtraction plugin, String id){
        String path = "hordes." + id + ".nodes";
        if (!plugin.hordesFile.getConfig().contains(path)) return null;
        Horde horde = new Horde(id);
        Set<String> section = plugin.hordesFile.getConfig().getConfigurationSection(path).getKeys(false);
        for (String key : section){
            Location loc = plugin.fromString(key);
            List<String> sClasses = plugin.hordesFile.getConfig().getStringList(path + "." + key + ".types");
            List<Class<? extends PiglinEntity>> types = new ArrayList<>();
            try {
                for (String entry : sClasses){
                    Class<? extends PiglinEntity> clazz = (Class<? extends PiglinEntity>) Class.forName(entry);
                    types.add(clazz);
                }
            }catch (Exception e){}
            int toSpawn = plugin.hordesFile.getConfig().getInt(path + "." + key + ".toSpawn");
            SpawnNode node = new SpawnNode(loc, toSpawn, types);
            horde.addNode(node);
        }
        String triggerString = plugin.hordesFile.getConfig().getString("hordes." + id + ".trigger");
        if (triggerString == null) return horde;
        String[] split = triggerString.split("/");
        TriggerType type = TriggerType.valueOf(split[1]);
        Trigger trigger = new Trigger(horde, split[0],type);
        horde.setTrigger(trigger);
        return horde;
    }
}
