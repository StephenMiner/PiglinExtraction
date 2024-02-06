package piglinextraction.me.stephenminer.mobs.boss.encounters;

import org.bukkit.Location;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.mobs.boss.Reinforcement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WarlordEncounter {
    private List<Location> reinforcements;
    private Room room;
    private Flag flag;
    private String reinforcementStr;
    /*

    level:
        room:
            on-enter:
                warlord=loc1/loc2/loc3-type1,type2,type3


     */
    /**
     *
     * @param str formatted as "warlord=room=flag=loc1/loc2/loc3-type1,type2,type3=TBD"
     */
    public WarlordEncounter(String str){
        reinforcementStr = loadEssentialData(str);
    }


    /**
     *
     * @param str formatted as "warlord=room=flag=loc1/loc2/loc3-type1,type2,type3=TBD"
     * @return trimmed down str only including reinforcement data
     * formatted as loc1/loc2/loc3-type1,type2,type3
     */
    private String loadEssentialData(String str){
            String[] split = str.split("=");
            this.room = Room.BY_IDS.get(split[1]);
            this.flag = Flag.valueOf(split[2]);
            return split[split.length-1];
    }

    /**
     *
     * @param str formatted as "loc1/loc2/...-type1,type2,..."
     * @return list of Reinforcement objects based of str data
     */
    public List<Reinforcement> loadReinforcements(String str){
        List<Reinforcement> out = new ArrayList<>();
        return out;
    }


    public enum Flag{
        ON_ENTER(),
        ON_EXIT();
    }
}
