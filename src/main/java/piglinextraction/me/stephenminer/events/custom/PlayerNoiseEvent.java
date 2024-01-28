package piglinextraction.me.stephenminer.events.custom;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class PlayerNoiseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Location epicenter;
    private final int intensity;
    private final List<LivingEntity> nearbyEntities;

    public PlayerNoiseEvent(Player player, int intensity){
        this.player = player;
        epicenter = player.getLocation();
        this.intensity = intensity;
        nearbyEntities = initNearbyEntities();
    }
    public PlayerNoiseEvent(Location epicenter, int intensity){
        player = null;
        this.epicenter = epicenter;
        this.intensity = intensity;
        nearbyEntities = initNearbyEntities();
    }

    public Location getEpicenter(){ return epicenter; }
    public Player getPlayer(){
        return player;
    }
    public boolean hasPlayer(){ return player != null; }

    private List<LivingEntity> initNearbyEntities(){
        List<LivingEntity> entities = new ArrayList<>();
        Collection<Entity> elist = epicenter.getWorld().getNearbyEntities(epicenter,intensity, intensity,intensity);
        for (Entity e : elist)
            if (e instanceof LivingEntity entity) {
                if (hasPlayer())
                    if (e instanceof Player) continue;
                entities.add(entity);
            }
        return entities;
    }

    public List<LivingEntity> getNearbyEntities(){
        return nearbyEntities;
    }

    public int getIntensity(){
        return intensity;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
