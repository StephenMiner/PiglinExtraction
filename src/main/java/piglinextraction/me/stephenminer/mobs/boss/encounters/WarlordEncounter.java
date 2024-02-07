package piglinextraction.me.stephenminer.mobs.boss.encounters;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.boss.Reinforcement;
import piglinextraction.me.stephenminer.mobs.boss.Warlord;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public class WarlordEncounter extends RoomEncounter{

    private Location spawn;
    private String reinforcementStr;
    /*

    level:
        room:
            on-enter:
                warlord=loc1/loc2/loc3-type1,type2,type3


     */
    /**
     *
     * @param str formatted as "warlord=room=flag=spawn=loc1/loc2/loc3-type1,type2,type3=TBD"
     */
    public WarlordEncounter(String str){
        super("warlord");
        reinforcementStr = loadEssentialData(str);
    }


    /**
     *
     * @param str formatted as "id=room=flag=loc1/loc2/loc3-type1,type2,type3=TBD"
     * @return trimmed down str only including reinforcement data
     * formatted as loc1/loc2/loc3-type1,type2,type3=loc1/loc2/...
     *
     */
    private String loadEssentialData(String str){
        String[] split = str.split("=");
        this.room = Room.BY_IDS.get(split[1]);
        this.flag = RoomEncounter.Flag.valueOf(split[2]);
        this.spawn = plugin.fromString(split[3]);
        StringBuilder out = new StringBuilder();
        for (int i = 4; i < split.length; i++) out.append(split[i]).append("=");
        out.deleteCharAt(out.length());
        return out.toString();
    }

    /**
     *
     * @param str formatted as "loc1/loc2/...-type1,type2,...=loc1/loc2/...-type1,..."
     * @return list of Reinforcement objects based of str data
     */
    public List<Reinforcement> loadReinforcements(String str){
        List<Reinforcement> out = new ArrayList<>();
        String[] split = str.split("=");
        for (String entry : split){
            out.add(new Reinforcement(entry));
        }
        return out;
    }

    public void activate(){
        Warlord warlord = new Warlord(plugin,spawn,loadReinforcements(reinforcementStr));
    }




    public RoomEncounter.Flag getFlag(){ return flag; }
    public Location getSpawn(){ return spawn; }
    public Room getRoom(){ return room; }

    @Override
    public String toString(){
        return super.toString() + "=" + reinforcementStr;
    }




}
