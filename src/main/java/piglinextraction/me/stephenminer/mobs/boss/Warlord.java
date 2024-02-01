package piglinextraction.me.stephenminer.mobs.boss;

import org.bukkit.Location;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.ArrayList;
import java.util.List;

public class Warlord extends PiglinEntity {
    private List<Location> reinforcePoints;
    public Warlord(PiglinExtraction plugin, PiglinType type, Location spawn, int lightActivation) {
        super(plugin, type, spawn, lightActivation);
        reinforcePoints = new ArrayList<>();
    }


    @Override
    public void target() {
        super.target();
    }




}
