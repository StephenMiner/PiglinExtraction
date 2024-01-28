package piglinextraction.me.stephenminer.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.objectives.Objective;

import java.util.UUID;
import java.util.List;

public class ObjectiveFinishEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Level level;
    private final Objective objective;

    public ObjectiveFinishEvent(Level level, Objective objective){
        this.level = level;
        this.objective = objective;
    }





    public String getId(){ return level.getId(); }
    public Level getLevel(){ return level; }
    public List<UUID> getPlayers(){ return level.getPlayers(); }
    public Objective getObjective(){ return objective; }



    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
