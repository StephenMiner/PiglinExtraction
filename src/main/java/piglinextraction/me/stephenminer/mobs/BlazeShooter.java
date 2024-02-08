package piglinextraction.me.stephenminer.mobs;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.Rotation;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class BlazeShooter extends PiglinEntity {
    /**
     * how many times blaze will shoot in a given attack
     */
    private final int fireRate;

    /**
     * shooting cooldown value
     */
    private final int shootCD;

    /**
     * whether blaze is attacking or not
     */
    private boolean attacking;

    /**
     * tick tracker for shooting cooldown
     */
    private int count;


    public BlazeShooter(PiglinExtraction plugin, Location loc){
        super(plugin, PiglinType.BLAZE,loc,0);
        fireRate = 40;
        shootCD = 2*fireRate;

    }

    @Override
    public void target(){
        new BukkitRunnable(){
            double y = mob.getLocation().getY();
            @Override
            public void run(){
                y = mob.getLocation().getY();
                if (mob.getTarget() != null) {
                    mob.getLocation().setY(y);
                }
                if (count>= shootCD){
                    attack();
                    count = 0;
                }
                if (!attacking) count++;
            }
        }.runTaskTimer(plugin,1,1);

    }

    private void attack(){
        attacking = true;
        List<Vector> points = new ArrayList<>();
        Rotation rotation = new Rotation();
        Location loc = mob.getLocation();
        double pitch = (loc.getPitch()) * 0.017453292F;
        double yaw = -loc.getYaw() * 0.017453292F;
        for (double radius = 0; radius < 2; radius+=0.5){
            for (double angle = 0; angle <= 2*Math.PI; angle += Math.PI/16){
                double x = radius*Math.cos(angle);
                double z = radius*Math.sin(angle);
                Vector vec = new Vector(x,0,z);
                rotation.rotateAroundAxisX(vec, yaw);
                rotation.rotateAroundAxisY(vec,pitch);
                points.add(vec);
            }
        }
        World world = mob.getWorld();
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (count > fireRate){
                    attacking = false;
                    this.cancel();
                    return;
                }
                int roll = ThreadLocalRandom.current().nextInt(points.size());
                launchLine(points.get(roll).toLocation(world),50);
                count++;
            }
        }.runTaskTimer(plugin,1,2);
    }


    private void launchLine(Location start, int range){
        Location center = start.clone();
        for (int i = 0; i < range; i++){
            if (center.getBlock().getBoundingBox().overlaps(BoundingBox.of(center.toVector(),center.toVector()))){
                center.getWorld().playSound(center, Sound.BLOCK_BASALT_PLACE,0.5f,1);
                return;
            }
            Player hit = hitPlayer(center);
            if (hit != null){
                hit.damage(2,mob);
                return;
            }
            center.getWorld().spawnParticle(Particle.FLAME,center,1);
            center.add(mob.getLocation().getDirection());

        }
    }
    private Player hitPlayer(Location hit){
        Vector min = new Vector(hit.getX()-0.25,hit.getY()-0.25, hit.getZ()-0.25);
        Vector max = new Vector(hit.getX()+0.25,hit.getY()+0.25,hit.getZ() + 0.25);
        BoundingBox bounds = BoundingBox.of(min,max);
        Collection<Entity> near = hit.getWorld().getNearbyEntities(hit,1,1,1);
        for (Entity entity : near){
            if (entity instanceof Player player && entity.getBoundingBox().overlaps(bounds)) return player;
        }
        return null;
    }

}
