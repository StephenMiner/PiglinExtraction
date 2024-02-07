package piglinextraction.me.stephenminer.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import piglinextraction.me.stephenminer.levels.rooms.Room;

public class RoomEnterEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Room room;
    private final Player player;

    public RoomEnterEvent(Room room, Player player){
        this.room = room;
        this.player = player;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer(){ return player; }
    public Room getRoom(){ return room; }
}
