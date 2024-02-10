package piglinextraction.me.stephenminer.mobs.boss;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.mobs.MobTranslator;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Reinforcement {
    private PiglinExtraction plugin;
    private List<Class<? extends PiglinEntity>> types;
    private List<Location> locs;
    public Reinforcement(List<Class<? extends PiglinEntity>> types, List<Location> locs){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        init(types, locs);
    }

    /**
     *
     * @param sTypes formatted as type1,type2,... ex: "PiglinGrunt,PiglinKnight,Necromancer"
     * @param sLocs formatted as loc1/loc2/...
     */
    public Reinforcement(String sTypes, String sLocs){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        init(parseTypes(sTypes), parseLocs(sLocs));
    }

    /**
     *
     * @param str formatted as "loc1/loc2/...%type1,type2,..."
     */
    public Reinforcement(String str){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        String[] unbox = str.split("%");
        init(parseTypes(unbox[1]),parseLocs(unbox[0]));
    }
    private void init(List<Class<? extends PiglinEntity>> types, List<Location> locs){
        this.types = types;
        this.locs = locs;
    }


    public void reinforce(Level level){
        for (Location loc : locs){
            try {
                System.out.println(22);
                PiglinEntity entity = types.get(ThreadLocalRandom.current().nextInt(types.size())).getConstructor(PiglinExtraction.class, Location.class).newInstance(plugin, loc);
                if (level != null) level.getSpawned().put(entity.getMob().getUniqueId(),entity);
            }catch (Exception e){ e.printStackTrace(); }
        }
    }




    public String formattedTypes(){
        StringBuilder out = new StringBuilder();
        types.forEach(str->out.append(str).append(","));
        out.deleteCharAt(out.length()-1);
        return out.toString();
    }
    public String formattedLocs(){
        StringBuilder out = new StringBuilder();
        locs.forEach(loc->out.append(plugin).append("/"));
        out.deleteCharAt(out.length()-1);
        return out.toString();
    }

    /**
     *
     * @param str formatted as "loc1/loc2/..."
     * @return list of Locations
     */
    private List<Location> parseLocs(String str){
        String[] unbox = str.split("/");
        List<Location> locs = new ArrayList<>();
        for (String entry : unbox){
            try {
                locs.add(plugin.fromString(entry));
            }catch (Exception e){
                e.printStackTrace();
                plugin.getLogger().warning("Failed to load location for a warlord encounter!");
                plugin.getLogger().warning("Offending string: " + entry);
            }
        }
        return locs;
    }

    /**
     *
     * @param str formatted as "type1,type2,..."
     * @return List of PiglinEntity types
     */
    private List<Class<? extends PiglinEntity>> parseTypes(String str){
        String[] unbox = str.split(",");
        MobTranslator translator = new MobTranslator();
        List<Class<? extends PiglinEntity>> types = new ArrayList<>();

        for (String entry : unbox){
            try{
                Class<? extends PiglinEntity> type = translator.parseString(entry);
                types.add(type);
            }catch (Exception e){
                e.printStackTrace();
                plugin.getLogger().warning("Failed to load PiglinEntity type for warlord encounter");
                plugin.getLogger().warning("Offending String: " + entry);
            }
        }
        return types;
    }

    @Override
    public String toString(){
        return formattedLocs() + "-" + formattedTypes();
    }


}
