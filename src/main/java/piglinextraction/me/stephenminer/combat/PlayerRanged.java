package piglinextraction.me.stephenminer.combat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;

import java.util.List;


public class PlayerRanged implements Listener {
    private final PiglinExtraction plugin;


    public PlayerRanged(PiglinExtraction plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        Player player = event.getPlayer();
        if (plugin.rangedWeaponsP.containsKey(player.getUniqueId())) {
            RangedWeapon weapon = plugin.rangedWeaponsP.get(player.getUniqueId());
            if (compareItems(event.getItem(), weapon.getItem())) {
                if (event.getAction().isLeftClick())
                    weapon.reload();
                else if (event.getAction().isRightClick())
                    weapon.shoot();
            }
        }
    }

    @EventHandler
    public void projectileHeadShots(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Entity entity = event.getHitEntity();
        if (projectile.getShooter() instanceof Player player)
            if (entity != null && entity instanceof LivingEntity livingEntity)
                try {
                    String name = projectile.getName();
                    if (!plugin.rangedWeaponsP.containsKey(player.getUniqueId())) return;
                    RangedWeapon rangedWeapon = plugin.rangedWeaponsP.get(player.getUniqueId());
                    if (name.equalsIgnoreCase(rangedWeapon.getProjId())) {
                        Location hitLoc = projectile.getLocation();
                        boolean headshot = (hitLoc.getY() - livingEntity.getLocation().getY() > 1.6d);
                        rangedWeapon.damage(livingEntity, projectile, headshot);
                        projectile.remove();
                    }
                } catch (Exception ignored) {
                }
    }



    @EventHandler
    public void updateAmmo(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        player.setLevel(0);
        int slot = event.getNewSlot();
        if (player.getInventory().getItem(slot) == null) return;
        if (plugin.rangedWeaponsP.containsKey(player.getUniqueId())) {
            RangedWeapon weapon = plugin.rangedWeaponsP.get(player.getUniqueId());
            if (compareItems(weapon.getItem(), player.getInventory().getItem(slot)))
                player.setLevel(weapon.getAmmo());
        }
    }


    private boolean compareItems(ItemStack item1, ItemStack item2) {
        if (item1.hasItemMeta() && item2.hasItemMeta())
            if (item2.getItemMeta().hasLore() && item1.getItemMeta().hasLore()) {
                List<String> lore1 = item1.getItemMeta().getLore();
                List<String> lore2 = item2.getItemMeta().getLore();
                return (lore1.containsAll(lore2));
            }
        return false;
    }
}
