package piglinextraction.me.stephenminer.combat;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;


public class RayTrace {
    private Player player;

    public RayTrace(){
        this(null);
    }
    public RayTrace(Player player){
        this.player = player;
    }


    public RayTraceResult rayTrace(double maxDistance, double raySize){
        Vector start = player.getEyeLocation().toVector();
        Vector dir = player.getEyeLocation().getDirection().clone().normalize().multiply(maxDistance);
        BoundingBox box = BoundingBox.of(start, start).expandDirectional(dir).expand(raySize);
        Collection<Entity> entities = player.getWorld().getNearbyEntities(box);

        Entity nearestEntity = null;
        RayTraceResult nearestHitResult = null;
        double nearDistSq = Double.MAX_VALUE;

        for (Entity entity : entities){
            if (entity.equals(player)) continue;
            BoundingBox entityBox = entity.getBoundingBox().expand(raySize);
            RayTraceResult hitResult = entityBox.rayTrace(start, dir, maxDistance);
            if (hitResult != null){
                double distSq = start.distanceSquared(hitResult.getHitPosition());
                if (distSq < nearDistSq){
                    nearestEntity = entity;
                    nearestHitResult = hitResult;
                    nearDistSq = distSq;
                }
            }
        }
        return nearestEntity != null ? new RayTraceResult(nearestHitResult.getHitPosition(), nearestEntity, nearestHitResult.getHitBlockFace()) : null;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

}
