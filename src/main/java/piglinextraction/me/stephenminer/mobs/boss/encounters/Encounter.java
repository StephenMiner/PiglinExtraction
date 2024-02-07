package piglinextraction.me.stephenminer.mobs.boss.encounters;

import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;

public class Encounter {
    protected final PiglinExtraction plugin;
    protected final String id;

    public Encounter(String id){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.id = id;
    }




    @Override
    public String toString(){
        return id;
    }
}
