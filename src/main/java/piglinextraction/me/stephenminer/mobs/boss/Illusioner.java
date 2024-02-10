package piglinextraction.me.stephenminer.mobs.boss;

import org.bukkit.Location;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.PiglinType;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

public class Illusioner extends PiglinEntity {

    public Illusioner(PiglinExtraction plugin, PiglinType type, Location spawn, int lightActivation) {
        super(plugin, type, ArmorPiercing.MEDIUM, spawn,lightActivation);
    }
}
