package piglinextraction.me.stephenminer.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import piglinextraction.me.stephenminer.events.custom.RoomEnterEvent;
import piglinextraction.me.stephenminer.levels.rooms.Room;

public class EncounterEvents implements Listener {

    @EventHandler
    public void enterRoom(RoomEnterEvent event){
        Room room = event.getRoom();
        room.listenEncounters(true);
    }
}
