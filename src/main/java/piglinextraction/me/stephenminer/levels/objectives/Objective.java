package piglinextraction.me.stephenminer.levels.objectives;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.events.custom.ObjectiveFinishEvent;
import piglinextraction.me.stephenminer.levels.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Objective {
    protected final ObjectiveType type;
    protected final PiglinExtraction plugin;
    protected final List<Location> potentialSpawns;
    protected boolean complete;
    protected boolean kill;
    protected String name, id;

    protected Player collected;



    public Objective(PiglinExtraction plugin, String id, ObjectiveType type){
        this.type = type;
        this.plugin = plugin;
        potentialSpawns = new ArrayList<>();
        this.id = id;
        Bukkit.broadcastMessage("Created Objective object");
    }


    public void init(){

    }


    public void setComplete(boolean complete){
        this.complete = complete;
    }
    public boolean isComplete(){
        return complete;
    }
    public void setKill(boolean kill){ this.kill = kill; }
    public boolean kill(){
        return kill;
    }

    public void addSpawn(Location spawn){
        potentialSpawns.add(spawn);
    }
    public void removeSpawn(Location spawn){
        String sSpawn = plugin.fromBlockLoc(spawn);
        for (int i = potentialSpawns.size()-1; i >= 0; i--){
            StringBuilder builder = new StringBuilder(plugin.fromBlockLoc(potentialSpawns.get(i)));
            if (builder.toString().equals(sSpawn)) {
                potentialSpawns.remove(i);
                return;
            }
        }
    }
    public List<Location> getSpawns(){
        return potentialSpawns;
    }

    public ObjectiveType getType(){
        return type;
    }

    public boolean save(String levelId){
        String path = "levels." + levelId;
        if (!plugin.levelsFile.getConfig().contains(path)) return false;
        List<String> locStrings = new ArrayList<>();
        for (Location loc : potentialSpawns){
            locStrings.add(plugin.fromBlockLoc(loc));
        }
        plugin.levelsFile.getConfig().set(path + ".objs." + id + ".spawns", locStrings);
        plugin.levelsFile.getConfig().set(path + ".objs." + id + ".type",type);
        plugin.levelsFile.saveConfig();
        return true;
    }

    public void notifyDone(){
        for (Level level : Level.levels){
            if (level.getObjectives().contains(this)){
                String msg;
                if (level.getCompleted() == level.getObjectives().size()){
                    msg = "Return to the extraction point where you entered!";
                } else msg = level.getCompleted() + "/" + level.getObjectives().size() + " objectives completed";
                for (UUID uuid : level.getPlayers()){
                    Player player = Bukkit.getPlayer(uuid);
                    player.sendMessage(msg);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                }
                ObjectiveFinishEvent event = new ObjectiveFinishEvent(level, this);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    public void setWhoCompleted(Player player) {
        this.collected = player;
    }
    public Player getWhoCompleted(){ return collected; }

    public String getDisplay(){ return type.collectionId().replace('_',' '); }

    public String getStatus(){ return ""; }

}
