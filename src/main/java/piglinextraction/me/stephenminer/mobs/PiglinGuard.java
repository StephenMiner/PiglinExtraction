package piglinextraction.me.stephenminer.mobs;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;

public class PiglinGuard extends PiglinEntity{

    public PiglinGuard(PiglinExtraction plugin, Location spawn){
        super(plugin, PiglinType.GUARD, spawn,1);
        activated = true;
        target();
    }

    @Override
    public void target() {
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (mob.isDead()){
                    this.cancel();
                    return;
                }
                mob.setTarget(null);
                if (count == 400) {
                    count = 0;
                    sweep();
                }
                count++;
            }
        }.runTaskTimer(plugin, 200, 1);
    }


    private void sweep(){
        new BukkitRunnable(){
            final World world = mob.getWorld();
            int count = 0;
            int radius = 0;
            @Override
            public void run(){
                if (mob.isDead() || radius > 10){
                    this.cancel();
                    return;
                }

                if (count == 20){
                    for (int i = 0; i <= 2 * Math.PI; i+= Math.PI/40){
                        double x = radius * Math.cos(i);
                        double y = 1.5;
                        double z = radius * Math.sin(i);
                        Location loc = mob.getLocation().clone().add(x,y,z);
                        world.spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 0);
                        for (Entity entity : loc.getNearbyEntities(2,2,2)){
                            if (entity instanceof Player player && loc.getBlock().getBoundingBox().overlaps(player.getBoundingBox())){
                                Bukkit.broadcastMessage("HIT");
                                world.playSound(mob, Sound.EVENT_RAID_HORN, 100, 1);
                                for (Entity e : mob.getNearbyEntities(100, 100, 100)){
                                    for (PiglinEntity piglin : PiglinEntity.cache){
                                        if (piglin.entityIsSimilar(e)){
                                            piglin.activate(true);
                                        }
                                    }
                                }
                                this.cancel();
                                return;
                            }
                        }
                        radius++;
                    }
                    count = 0;
                }
                count++;
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}
