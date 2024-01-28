package piglinextraction.me.stephenminer.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.objectives.Objective;
import piglinextraction.me.stephenminer.levels.rooms.Door;

import java.util.List;
import java.util.UUID;

public class UseDoorEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Door door;
    private final boolean opening;
    private final Player player;

    public UseDoorEvent(Door door, boolean opening, Player player){
        this.door = door;
        this.opening = opening;
        this.player = player;
    }





    public Door getDoor(){ return door; }
    public boolean isOpening(){ return opening; }
    public Player getPlayer(){ return player; }



    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
