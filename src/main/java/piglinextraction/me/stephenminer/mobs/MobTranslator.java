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
        str = str.toString();
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
}
