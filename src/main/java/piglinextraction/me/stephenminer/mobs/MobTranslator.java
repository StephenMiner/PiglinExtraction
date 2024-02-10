package piglinextraction.me.stephenminer.mobs;

import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.boss.Warlord;

public class MobTranslator {
    private final PiglinExtraction plugin;

    public MobTranslator(){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
    }


    public Class<? extends PiglinEntity> parseString(String str){
        str = str.toLowerCase();
        return switch (str){
            case "warlord" -> Warlord.class;
            case "piglingrunt", "grunt" -> PiglinGrunt.class;
            case "blazeshooter","blaze" -> BlazeShooter.class;
            case "necromancer" -> Necromancer.class;
            case "piglinknight", "knight" -> PiglinKnight.class;
            case "piglinguard","guard" -> PiglinGuard.class;
            default -> null;
        };
    }

    public String fromClass(Class<? extends  PiglinEntity> clazz){
        if (clazz.equals(PiglinGrunt.class)) return "grunt";
        if (clazz.equals(PiglinKnight.class)) return "knight";
        if (clazz.equals(BlazeShooter.class)) return "blaze";
        if (clazz.equals(Necromancer.class)) return "necromancer";
        if (clazz.equals(PiglinGuard.class)) return "guard";
        if (clazz.equals(Warlord.class)) return "warlord";
        return null;
    }
}
