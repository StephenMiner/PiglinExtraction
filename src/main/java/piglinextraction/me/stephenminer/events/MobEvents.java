package piglinextraction.me.stephenminer.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import org.bukkit.event.entity.EntityDeathEvent;
import piglinextraction.me.stephenminer.events.custom.PlayerNoiseEvent;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.levels.spawners.Node;
import piglinextraction.me.stephenminer.mobs.Necromancer;
import piglinextraction.me.stephenminer.mobs.PiglinEntity;

import java.util.UUID;

public class MobEvents implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event){
        if (event.getEntity() instanceof Player) return;
        if (event.getEntity() instanceof LivingEntity livingEntity){
            for (PiglinEntity piglinEntity : PiglinEntity.cache){
                if (livingEntity.equals(piglinEntity.getMob()) && !event.isCancelled()){
                    System.out.println(1);
                    if (event.getDamager() instanceof Player player)
                        piglinEntity.setTarget(player);
                    else if(event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player player)
                        piglinEntity.setTarget(player);
                    piglinEntity.activate(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event){
        if (event.getEntity() instanceof Mob entity) {
            for (int i = 0; i < PiglinEntity.cache.size(); i++) {
                PiglinEntity piglinEntity = PiglinEntity.cache.get(i);
                if (piglinEntity.getMob().equals(entity)){
                    if (piglinEntity instanceof Necromancer necromancer){
                        necromancer.kill();
                    }
                    PiglinEntity.cache.remove(piglinEntity);
                    event.getDrops().clear();
                    Bukkit.getPluginManager().callEvent(new PlayerNoiseEvent(event.getEntity().getLocation(), 2));
                }
            }
            UUID uuid = entity.getUniqueId();
            for (Level lvl : Level.levels){
                lvl.getSpawned().remove(uuid);
            }
        }
    }
    //OLD TEST
/*
    @EventHandler
    public void knightBlock(EntityDamageByEntityEvent event){
        Entity entity = event.getEntity();
        if (entity.hasMetadata("knight")){
            double dYaw = validateAngle(event.getDamager().getLocation().getYaw());
            double eYaw = validateAngle(entity.getLocation().getYaw());
            double dif = Math.abs( dYaw - eYaw );
            System.out.println(dYaw + "," + eYaw);
            if (dif <= 176) Bukkit.broadcastMessage("Back-Shot");
            else Bukkit.broadcastMessage("womp womp");
        }
    }

 */

    private double validateAngle(double angle){
        if (angle < 0 ){
            while (angle < 0) angle+=360;
        }else if (angle > 360){
            while (angle > 360) angle -= 360;
        }
        return angle;
    }

    @EventHandler
    public void soundEvent(PlayerNoiseEvent event){
        for (LivingEntity entity : event.getNearbyEntities()){
            for (PiglinEntity piglinEntity : PiglinEntity.cache){
                if (entity.equals(piglinEntity.getMob())){
                    piglinEntity.activate(true);
                }
            }
        }
    }
}
