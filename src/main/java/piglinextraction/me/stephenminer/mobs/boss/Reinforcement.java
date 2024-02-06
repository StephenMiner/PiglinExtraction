package piglinextraction.me.stephenminer.mobs.boss;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Reinforcement {
    private PiglinExtraction plugin;
    private List<Class<? extends PiglinEntity>> types;
    private List<Location> locs;
    public Reinforcement(List<Class<? extends PiglinEntity>> types, List<Location> locs){
        init(types, locs);
    }

    /**
     *
     * @param sTypes formatted as type1,type2,... ex: "PiglinGrunt,PiglinKnight,Necromancer"
     * @param sLocs formatted as loc1/loc2/...
     */
    public Reinforcement(String sTypes, String sLocs){
        init(parseTypes(sTypes), parseLocs(sLocs));
    }

    /**
     *
     * @param str formatted as "loc1/loc2/...-type1,type2,..."
     */
    public Reinforcement(String str){
        String[] unbox = str.split("-");
        init(parseTypes(unbox[1]),parseLocs(unbox[0]));
    }
    private void init(List<Class<? extends PiglinEntity>> types, List<Location> locs){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.types = types;
        this.locs = locs;
    }


    public void reinforce(){
        for (Location loc : locs){
            try {
                PiglinEntity entity = types.get(ThreadLocalRandom.current().nextInt(types.size())).getConstructor(PiglinExtraction.class, Location.class).newInstance(plugin, loc);
            }catch (Exception e){ e.printStackTrace(); }
        }
    }




    public String formattedTypes(){
        StringBuilder out = new StringBuilder();
        types.forEach(str->out.append(str).append(","));
        out.deleteCharAt(out.length());
        return out.toString();
    }
    public String formattedLocs(){
        StringBuilder out = new StringBuilder();
        locs.forEach(loc->out.append(plugin).append("/"));
        out.deleteCharAt(out.length());
        return out.toString();
    }

    private List<Location> parseLocs(String sLocs){
        List<Location> locs = new ArrayList<>();
        String[] unbox = sLocs.split("/");
        for (String entry : unbox){
            locs.add(plugin.fromString(entry));
        }
        return locs;
    }
    private List<Class<? extends PiglinEntity>> parseTypes(String sTypes){
        List<Class<? extends PiglinEntity>> types = new ArrayList<>();
        String[] unbox = sTypes.split(",");
        for (String entry : unbox) {
            try {
                Class<? extends PiglinEntity> clazz = (Class<? extends PiglinEntity>) Class.forName(entry);
                types.add(clazz);
            }catch (Exception e){
                plugin.getLogger().log(Level.WARNING,"Failed to parse type " + entry + " for Reinforcements object. " + entry + " is not a valid Piglin Class Name!");
            }
        }
        return types;
    }

    @Override
    public String toString(){
        return formattedLocs() + "-" + formattedTypes();
    }


}
