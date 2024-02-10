package piglinextraction.me.stephenminer.weapons.mele;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.combat.RayTrace;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;

import java.util.List;
import java.util.logging.Level;

public class MeleWeapon {
    protected final PiglinExtraction plugin;
    protected String id;
    protected double range;
    protected float headsotMultiplier;
    protected double damage;
    protected final Player owner;
    protected ItemStack item;
    protected FixedMetadataValue attackTag;
    protected boolean charged;
    protected final double dummyDmg;
    protected double attackSpeed;
    protected int hitStagger;
    protected boolean chargeStagger;
    protected ArmorPiercing armorPierce;

    public MeleWeapon(PiglinExtraction plugin, Player owner) {
        this.plugin = plugin;
        this.owner = owner;
        attackTag = new FixedMetadataValue(plugin, "tag");
        dummyDmg = 10;
    }


    public void giveItem() {
        try {
            owner.getInventory().addItem(item);
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Attempted to give " + owner.displayName() + " item, but their inventory was full!");
        }
    }

    public void attack(LivingEntity entity, boolean headshot,boolean blocked) {
        double dmg = charged ? this.damage : this.damage/1.5;
        if (blocked) {
            dmg = 0;
        }
        double damage = headshot ? dmg * (headsotMultiplier) : dmg;
        entity.setMetadata(id, attackTag);
        boolean needsHeadshot = needsHeadshot(entity);
        if (needsHeadshot && !headshot){
            damage = 0.25;
            owner.playSound(owner, Sound.BLOCK_CHAIN_BREAK,4,1);
        }
        entity.damage(damage, owner);
        if (entity instanceof Mob mob){

            need_charge:
            {
                if ((needsHeadshot && !headshot) || (chargeStagger && !charged)) break need_charge;
                else staggerEntity(mob);
            }
        }
        Bukkit.broadcastMessage("headshot: " + headshot + " charged: " + charged + " Damage: " + damage + " Base-Damage: " + this.damage);
        charged = false;

    }
    private boolean needsHeadshot(LivingEntity living){
        if (living.hasMetadata("high-armor")){
            return armorPierce != ArmorPiercing.HIGH;
        }
        if (living.hasMetadata("medium-armor")) {
            return armorPierce != ArmorPiercing.MEDIUM && armorPierce != ArmorPiercing.HIGH;
        }
        return false;
    }


    protected void trackAttackCooldown() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (owner.isOnline())
                    if (compareItems(item, owner.getInventory().getItemInMainHand())) {
                        charged = owner.getAttackCooldown() == 1;
                    } else charged = false;
                else charged = false;

            }
        }.runTaskTimerAsynchronously(plugin, 1, 1);
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

    public boolean canAttack(Entity entity) {
        RayTrace rayTrace = new RayTrace();
        rayTrace.setPlayer(owner);
        RayTraceResult result = rayTrace.rayTrace(range, 0.0);
        try {
            return entity.equals(result.getHitEntity());
        } catch (Exception e) {
            return false;
        }
    }

    protected void staggerEntity(Mob mob){
        mob.setAware(false);
        mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,hitStagger,999));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,hitStagger,999));
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (mob.isDead()){
                    this.cancel();
                    return;
                }
                if (count >= hitStagger) {
                    mob.setAware(true);
                    mob.removePotionEffect(PotionEffectType.SLOW);
                    mob.removePotionEffect(PotionEffectType.WEAKNESS);
                    this.cancel();
                    return;
                }
                count++;
            }
        }.runTaskTimer(plugin,0,1);
    }


    public ItemStack getItem() {
        return item;
    }

    public double getRange() {
        return range;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public String getId() {
        return id;
    }

    public boolean isCharged() {
        return charged;
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
    }

    public double getDummyDamage() {
        return dummyDmg;
    }

    public double getRealSpeed(){
        return (4*10d + attackSpeed*10) / 10d;
    }
}
