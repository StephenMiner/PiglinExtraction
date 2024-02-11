package piglinextraction.me.stephenminer.levels.builders;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.MobTranslator;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;
import piglinextraction.me.stephenminer.mobs.hordes.SpawnNode;
import piglinextraction.me.stephenminer.mobs.hordes.Trigger;
import piglinextraction.me.stephenminer.mobs.hordes.TriggerType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class HordeBuilder {
    private final PiglinExtraction plugin;
    private final String id;

    public HordeBuilder(String id) {
        this.id = id;
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
    }


    private void loadTrigger(Horde horde, String str) {
        String[] split = str.split("/");
        TriggerType type = TriggerType.valueOf(split[1]);
        Trigger trigger = new Trigger(horde, split[0], type);
        horde.setTrigger(trigger);
    }

    /**
     *
     * @param str formatted as "loc/toSpawn/spawnDelay/type1/type2/..."
     * @return SpawnNode
     */
    private SpawnNode builtNode(Horde horde, String str){
        String[] split = str.split("/");
        Location loc = plugin.fromString(split[0]);
        int toSpawn = Integer.parseInt(split[1]);
        int spawnDelay = Integer.parseInt(split[2]);
        List<Class<? extends PiglinEntity>> types = new ArrayList<>();
        MobTranslator translator = new MobTranslator();
        for (int i = 3; i < split.length; i++){
            Class<? extends PiglinEntity> clazz = translator.parseString(split[i]);
            types.add(clazz);
        }
        return new SpawnNode(horde, loc,toSpawn,spawnDelay,types);
    }

    public Horde build(){
        String path = "hordes." + id + ".nodes";
        if (!plugin.hordesFile.getConfig().contains(path)) return null;
        Horde horde = new Horde(id);
        List<String> sNodes = plugin.hordesFile.getConfig().getStringList(path);
        for (String entry : sNodes){
            SpawnNode node = builtNode(horde, entry);
            horde.addNode(node);
        }
        String triggerString = plugin.hordesFile.getConfig().getString("hordes." + id + ".trigger");
        if (triggerString == null) return horde;
        loadTrigger(horde, triggerString);
        return horde;
    }



}
