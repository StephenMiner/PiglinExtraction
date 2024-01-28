package piglinextraction.me.stephenminer.weapons;

import it.unimi.dsi.fastutil.Hash;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.Rotation;

import javax.swing.*;
import java.util.*;

public class Shield {
    public static final HashMap<UUID, Shield> shields = new HashMap<>();

    private final PiglinExtraction plugin;
    private ItemStack item;
    private int charges, maxCharge;
    private int pushCost = 2;
    private double pushStrength;
    private int pushStagger;

    private int regen, regenCooldown;
    private boolean regenerating;

    /**
     *
     * @param charges shield's game durability
     * @param pushCost charges it costs to use "push" ability
     * @param regen rate at which charges are replenished per second
     * @param pushStagger time in ticks that affected monsters will be stunned
     * @param pushStrength how much will monsters be launched
     * @param regenCooldown time in ticks before charges will regenerate after losing charge
     */
    public Shield(int charges, int pushCost, int regen, double pushStrength, int pushStagger, int regenCooldown){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.charges = charges;
        this.maxCharge = charges;
        this.pushCost = pushCost;
        this.regen = regen;
        this.pushStrength = pushStrength;
        this.pushStagger = pushStagger;
        this.regenCooldown = regenCooldown;
        generateItem();
    }

    public Shield(){
        this(8,2,20,1.05,45,10);
    }

    private void generateItem(){
        ItemStack item = new ItemStack(Material.SHIELD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Dwarven Shield");
        item.setItemMeta(meta);
        this.item = item;
    }


    public void block(EntityDamageEvent event){
        if (canBlock()){
            event.setCancelled(true);
            charges-=1;
        }
    }

    public void push(Player pusher){
        Location loc = pusher.getEyeLocation();
        World world = pusher.getWorld();
        double pitch = (loc.getPitch()) * 0.017453292F;
        double yaw = -loc.getYaw() * 0.017453292F;
        Rotation rotation = new Rotation();
        Set<UUID> hitEntities = new HashSet<>();
        world.spawnParticle(Particle.SWEEP_ATTACK,loc.clone().add(loc.getDirection()),1);
        for (double radius = 0; radius <= 1.5; radius+=0.5) {
            for (double angle = 0; angle <= Math.PI; angle += Math.PI / 8) {
                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);
                Vector vec = new Vector(x,0,z);
                rotation.rotateAroundAxisX(vec,pitch);
                rotation.rotateAroundAxisY(vec,yaw);
                Location nLoc = loc.clone().add(vec);
                Mob hit = getHit(nLoc);
                if (hit != null){
                    hitEntities.add(hit.getUniqueId());
                    staggerEntity(hit);
                    Vector dif = pusher.getLocation().clone().subtract(hit.getLocation()).toVector();
                    dif.multiply(-1*pushStrength);
                    hit.setVelocity(dif);
                }

            }
        }
        if (!hitEntities.isEmpty()) world.playSound(loc, Sound.BLOCK_CHAIN_PLACE,1,1);
    }

    private Mob getHit(Location point){
        Vector max = new Vector(point.getX()+0.25,point.getY(),point.getZ()+0.25);
        Vector min = new Vector(point.getX()-0.25,point.getY(),point.getZ()-0.25);
        BoundingBox bounds = BoundingBox.of(min,max);
        List<Entity> entities = point.getWorld().getNearbyEntities(point,1,1,1).stream()
                .filter(e->e instanceof Mob && !(e instanceof Player)).toList();
        for (Entity e : entities) {
            if (e.getBoundingBox().overlaps(bounds)) return ((Mob) e);
        }
        return null;
    }

    private void staggerEntity(Mob mob){
        mob.setAware(false);
        mob.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,pushStagger,999));
        mob.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,pushStagger,999));
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (mob.isDead()){
                    this.cancel();
                    return;
                }
                if (count >= pushStagger) {
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



    private int regenCd;
    private void regenerate(){
        regenCd = regenCooldown;
        if (regenerating) return;
        regenerating = true;
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (charges >= maxCharge){
                    regenerating = false;
                    this.cancel();
                    return;
                }
                if (regenCd <= 0){
                    if (count >= regen){
                        count = 0;
                        charges++;
                    }
                    count++;
                }else regenCd--;
            }
        }.runTaskTimer(plugin, 1, 1);
    }



    public boolean canBlock(){ return charges > 0; }

    public ItemStack getItem(){ return item;}

}
