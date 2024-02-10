package piglinextraction.me.stephenminer.combat;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.RayTraceResult;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.weapons.Shield;
import piglinextraction.me.stephenminer.weapons.mele.MeleWeapon;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;

import java.util.List;
import java.util.UUID;

public class PlayerMele implements Listener {
    private final PiglinExtraction plugin;
    private final RayTrace rayTrace;


    public PlayerMele(PiglinExtraction plugin){
        this.plugin = plugin;
        rayTrace = new RayTrace();
    }


    @EventHandler
    public void onSwing(PlayerArmSwingEvent event){
        Player player = event.getPlayer();
        World world = player.getWorld();
        if (!plugin.meleWeapons.containsKey(player.getUniqueId())) return;
        MeleWeapon weapon = plugin.meleWeapons.get(player.getUniqueId());
        rayTrace.setPlayer(player);
        ItemStack item = player.getInventory().getItemInMainHand();
        if (compareItems(item, weapon.getItem())) {
            RayTraceResult result = rayTrace.rayTrace(weapon.getRange(), 0d);
            try {
                Entity etity = result.getHitEntity();
                if (etity instanceof LivingEntity livingEntity) {
                    boolean headshot = result.getHitPosition().toLocation(world).getY() - livingEntity.getLocation().getY() > 1.55d;
                    if (item != null && compareItems(item, weapon.getItem())) {
                        // Bukkit.broadcastMessage("difference: " + (result.getHitPosition().toLocation(world).getY() - livingEntity.getLocation().getY()));
                        Bukkit.broadcastMessage("new cooldown: " + player.getAttackCooldown());
                        weapon.attack(livingEntity, headshot,!checkBackShot(livingEntity,player) && !headshot);
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }
    private boolean checkBackShot(Entity entity, Entity attacker){
        if (entity.hasMetadata("medium-armor") && entity instanceof Mob mob){
            ItemStack offhand = mob.getEquipment().getItemInOffHand();
            if (offhand.getType() != Material.SHIELD) return true;
            double dYaw = validateAngle(attacker.getLocation().getYaw());
            double eYaw = validateAngle(entity.getLocation().getYaw());
            double dif = Math.abs( dYaw - eYaw );
            return dif <= 176;
        }else return true;
    }
    private double validateAngle(double angle){
        if (angle < 0 ){
            while (angle < 0) angle+=360;
        }else if (angle > 360){
            while (angle > 360) angle -= 360;
        }
        return angle;
    }

    @EventHandler
    public void shieldUse(PlayerSwapHandItemsEvent event){
        ItemStack item = event.getMainHandItem();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (item != null && (item.getType() == Material.STICK || item.getType() == Material.SHIELD) && Shield.shields.containsKey(uuid)){
            Shield shield = Shield.shields.get(uuid);
            shield.push(player);
            event.setCancelled(true);
        }
    }

    @EventHandler (ignoreCancelled = true)
    public void damageEvent(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity livingEntity){
            if (!isInGame(player)) return;
            if (plugin.meleWeapons.containsKey(player.getUniqueId())){
                MeleWeapon weapon = plugin.meleWeapons.get(player.getUniqueId());
                if (livingEntity.hasMetadata(weapon.getId())){
                    Bukkit.broadcastMessage("final mele event dmg: " + event.getFinalDamage());
                    Bukkit.broadcastMessage("health: " + (livingEntity.getHealth() - event.getFinalDamage()));
                    event.setCancelled(false);
                    livingEntity.removeMetadata(weapon.getId(), plugin);
                    return;
                }
            }
            if (plugin.rangedWeaponsP.containsKey(player.getUniqueId())) {
                RangedWeapon weapon = plugin.rangedWeaponsP.get(player.getUniqueId());
                if (livingEntity.hasMetadata(weapon.getProjId())) {
                    event.setCancelled(false);
                    livingEntity.removeMetadata(weapon.getProjId(), plugin);
                    Bukkit.broadcastMessage("final ranged event dmg: " + event.getDamage());
                    Bukkit.broadcastMessage("health: " + (livingEntity.getHealth() - event.getFinalDamage()));
                    return;
                }
            }
            event.setCancelled(true);
        }
    }

    private boolean isInGame(Player player){
        for (Level level : Level.levels){
            if (level.getPlayers().contains(player.getUniqueId())) return true;
        }
        return false;
    }

    private boolean compareItems(ItemStack item1, ItemStack item2){
        if (item1.hasItemMeta() && item2.hasItemMeta())
            if (item2.getItemMeta().hasLore() && item1.getItemMeta().hasLore()){
                List<String> lore1 = item1.getItemMeta().getLore();
                List<String> lore2 = item2.getItemMeta().getLore();
                return  (lore1.containsAll(lore2));
            }
        return false;
    }



}
