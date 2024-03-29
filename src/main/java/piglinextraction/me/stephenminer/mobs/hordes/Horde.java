package piglinextraction.me.stephenminer.mobs.hordes;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
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
    private Level level;


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





    public void save(){
        String path = "hordes." + id;
        saveNodes();
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

        plugin.hordesFile.saveConfig();
    }
    private void saveNodes(){
        List<String> toSave = nodes.stream().map(SpawnNode::toString).toList();
        plugin.hordesFile.getConfig().set("hordes." + id + ".nodes",toSave);
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

    public Level getLevel(){
        return level;
    }
    public void setLevel(Level level){
        this.level = level;
    }

}
