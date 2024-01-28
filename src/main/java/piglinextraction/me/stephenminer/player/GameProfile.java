package piglinextraction.me.stephenminer.player;

import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.LevelGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameProfile {
    private final PiglinExtraction plugin;
    private final Player player;
    private final UUID uuid;
    private List<LevelGroup> completedLevels;
    private int kills;

    private int revives;

    private int wins;


    public GameProfile(PiglinExtraction plugin, Player player){
        completedLevels = new ArrayList<>();
        this.plugin = plugin;
        this.player = player;
        this.uuid = player.getUniqueId();
        if (hasProfile()) loadProfile();
        else saveProfile();
    }


    public void loadProfile(){
        String statPath = "players." + uuid + ".stats";
        String completePath = "players." + uuid + ".completed";
        kills = plugin.playersFile.getConfig().getInt(statPath + ".kills");
        revives = plugin.playersFile.getConfig().getInt(statPath + ".revives");
        wins = plugin.playersFile.getConfig().getInt(statPath + ".wins");
    }

    /**
     * @return true if players.yml contains uuid
     */
    public boolean hasProfile(){
        return plugin.playersFile.getConfig().contains("players." + uuid);
    }

    /**
     * Saves profile information (Duh) Also creates a new profile if a profile for the uuid doesn't already exist!
     */
    public void saveProfile(){
        String statPath = "players." + uuid + ".stats";
        plugin.playersFile.getConfig().set(statPath + ".kills", kills);
        plugin.playersFile.getConfig().set(statPath + ".revives", revives);
        plugin.playersFile.getConfig().set(statPath + ".wins", wins);
        plugin.playersFile.saveConfig();
    }




    public void setKills(int kills){
        this.kills = kills;
    }
    public void incrementKills(int increment){
        kills += increment;
    }
    public int getKills(){ return kills; }

    public void setRevives(int revives){
        this.revives = revives;
    }
    public void incrementRevives(int increment){
        revives += increment;
    }
    public int getRevives(){ return revives; }

    public void setWins(int wins){ this.wins = wins; }
    public void incrementWins(int increment){ wins += increment; }
    public int getWins(){ return wins; }

    public Player getPlayer(){ return player; }

    public List<LevelGroup> getCompletedLevels(){ return completedLevels; }

    public void completeLevel(LevelGroup group){
        completedLevels.add(group);
    }


}
