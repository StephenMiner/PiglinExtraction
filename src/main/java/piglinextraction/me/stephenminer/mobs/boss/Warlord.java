package piglinextraction.me.stephenminer.mobs.boss;

import org.bukkit.Location;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;
import piglinextraction.me.stephenminer.mobs.PiglinType;

import java.util.ArrayList;
import java.util.List;

public class Warlord extends PiglinEntity {
    private List<Reinforcement> reinforcements;
    private Level level;

    /**
     * How many times the warlord will be able to call reinforcements
     */
    private int reinforceUses;
    /**
     * tick interval between reinforcement spawns (first one happens automatically 5 seconds [20 ticks] after activation)
     */
    private int reinforceInt;
    public Warlord(PiglinExtraction plugin, Location spawn) {
        this(plugin, spawn, new ArrayList<>());
        reinforcements = new ArrayList<>();
    }

    public Warlord(PiglinExtraction plugin, Location spawn, List<Reinforcement> reinforcements) {
        super(plugin, PiglinType.WARLORD, spawn, 10);
        this.reinforcements = reinforcements;
    }

    @Override
    public void target() {
        super.target();
    }


    /**
     *
     * @return Game-Level warlord is in. May be null
     */
    public Level getLevel(){ return level; }

    public void setLevel(Level level){ this.level = level; }

    public void addReinforcements(Reinforcement reinforcement){ this.reinforcements.add(reinforcement); }
    public void removeReinforcements(Reinforcement reinforcement){ this.reinforcements.remove(reinforcement); }

    public void setReinforcements(List<Reinforcement> reinforcements){ this.reinforcements = reinforcements; }




}
