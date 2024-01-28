package piglinextraction.me.stephenminer.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.events.custom.ObjectiveFinishEvent;
import piglinextraction.me.stephenminer.events.custom.UseDoorEvent;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.rooms.Door;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;
import piglinextraction.me.stephenminer.mobs.hordes.Trigger;
import piglinextraction.me.stephenminer.mobs.hordes.TriggerType;

public class HordeEvents implements Listener {
    private final PiglinExtraction plugin;
    public HordeEvents(PiglinExtraction plugin){
        this.plugin = plugin;
    }


    @EventHandler
    public void artifactCollect(ObjectiveFinishEvent event){
        Level level = event.getLevel();
        for (Horde horde : level.getHordes()){
            String id = horde.getTrigger().triggerId();
            if (level.getId().equalsIgnoreCase(id)){
                int agitation = agitationLevel(level);
                if (agitation == horde.getTrigger().type().agitationlevel()){
                    horde.triggerHorde();
                }
            }
        }
    }

    @EventHandler
    public void onOpenDoor(UseDoorEvent event){
        Player player = event.getPlayer();
        Door door = event.getDoor();
        Level level = levelIn(player,door);
        if (level == null) return;
        for (Horde horde : level.getHordes()){
            Trigger trigger = horde.getTrigger();
            if (trigger.type() == TriggerType.DOOR && trigger.triggerId().equalsIgnoreCase(door.getId())){
                horde.triggerHorde();
            }
        }

    }



    private Level levelIn(Player player, Door door){
        for (Level level : Level.levels){
            if (level.isStarted()
                    && level.getPlayers().contains(player.getUniqueId())
                    && level.getRooms().contains(door.getRoom()))
                return level;
        }
        return null;
    }



    /**
     *
     * @param level
     * @return 0 = minor, 1 = agitated, 2 = severe
     **/
    public int agitationLevel(Level level){
        float ratio = ((float)level.getCompleted()) / level.getObjectives().size();
        if (ratio < 0.5) return 0;
        else if (ratio < 0.8) return 1;
        else return 2;
    }
}
