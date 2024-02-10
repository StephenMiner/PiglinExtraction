package piglinextraction.me.stephenminer.weapons.ranged;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.events.custom.PlayerNoiseEvent;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

import java.util.List;
import java.util.logging.Level;

public class RangedWeapon {
    protected final PiglinExtraction plugin;
    protected String id;
    protected String projId;

    protected int maxAmmo;
    protected int maxClip;

    protected int ammo;
    protected int clip;

    protected long cooldown;
    protected double shotInterval;
    protected boolean reloading;
    protected boolean forceReload;
    protected int reloadtime;

    protected final Player owner;
    protected ItemStack item;
    protected int itemslot;
    protected int pierce;

    protected double projDmg;
    protected float headshotMultiplier;
    protected ArmorPiercing armorPierce;

    protected FixedMetadataValue attackTag;

    public RangedWeapon(PiglinExtraction plugin, Player owner){
        this.plugin = plugin;
        this.owner = owner;
        attackTag = new FixedMetadataValue(plugin, "tag");
    }

    public void giveItem(){
        try{
            ItemStack i = item;
            i.setAmount(clip);
            owner.getInventory().setItem(itemslot, i);
            displayAmmo();
        }catch (Exception e){
            plugin.getLogger().log(Level.WARNING, "Attempted to give " + owner.displayName() + " item, but their inventory was full!");
        }
    }

    public void shoot(){
        if (clip < 1) return;
        if (clip == 1){
            if (reloading || System.currentTimeMillis() < cooldown) return;
            fire();
            reload();
            return;
        }
        fire();


    }
    protected void fire(){
        if (System.currentTimeMillis() < cooldown) return;
        clip--;
        ammo--;
        item.setAmount(clip <= 0 ? 1 : clip);
        owner.getInventory().setItem(itemslot, item);
        displayAmmo();
        plugin.getServer().getPluginManager().callEvent(new PlayerNoiseEvent(owner, 10));
        showCooldown();
    }

    public void reload(){
        if (reloading) return;
        reloading = true;
        clip = 0;
        owner.getInventory().setItem(itemslot, item);
        new BukkitRunnable(){
            int count = reloadtime;
            @Override
            public void run(){
                if (!owner.isOnline()){
                    this.cancel();
                    return;
                }
                if (forceReload) count = 0;
                if (count <= 0){
                    int newammo = ammo - maxClip;
                    if (newammo < 0)
                        clip = ammo;
                    else clip = maxClip;
                    Damageable damageable = (Damageable) item.getItemMeta();
                    damageable.setDamage(0);
                    item.setItemMeta(damageable);
                    if (clip > 0)
                        item.setAmount(clip);
                    else{
                        item.setAmount(1);
                        owner.sendMessage(ChatColor.RED + "You are out of ammo!");
                    }
                    owner.getInventory().setItem(itemslot, item);
                    reloading = false;
                    forceReload = false;
                    cancel();
                    return;
                }
                count--;
                float ratio = (float) count / reloadtime;
                Damageable damageable = (Damageable) item.getItemMeta();
                damageable.setDamage( (int) (item.getType().getMaxDurability() * ratio));
                item.setItemMeta(damageable);
                owner.getInventory().setItem(itemslot, item);

            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void damage(LivingEntity entity, Projectile projectile, boolean headshot){
        double dmg = headshot ? this.projDmg * headshotMultiplier : this.projDmg;
        entity.setMetadata(projId, attackTag);
        if (projectile.getShooter() instanceof Player player) {

            if (needsHeadshot(entity) && !headshot) {
                dmg = 0.5;
                player.playSound(player,Sound.BLOCK_CHAIN_BREAK,4,1);
            }
            entity.damage(dmg, player);
            Bukkit.broadcastMessage("val: " + headshot + " Damage: " + dmg + " Base-Damage: " + this.projDmg);
        }
    }

    private boolean needsHeadshot(LivingEntity living){
        if (living.hasMetadata("high-armor")){
            if (armorPierce == ArmorPiercing.HIGH)return false;
            else return true;
        }
        if (living.hasMetadata("medium-armor")) {
            if (armorPierce == ArmorPiercing.MEDIUM || armorPierce == ArmorPiercing.HIGH) return false;
            else return true;
        }
        return false;
    }

    public void showCooldown(){
        new BukkitRunnable(){
            int count = 0;
            final int max =(int) (shotInterval * 20);
            @Override
            public void run(){
                if (count >= max){
                    if (owner.isOnline()){
                        owner.playSound(owner.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
                    }
                    this.cancel();
                    return;
                }
                if (!owner.isOnline()) return;
                else{
                    owner.setExp((float) count / max);
                }
                count++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    public void displayAmmo(){
        owner.setLevel(ammo);
    }




    public void setAmmo(int ammunition){
        this.ammo = Math.min(ammo + ammunition, maxAmmo);
    }

    public void setItemSlot(int slot){
        this.itemslot = slot;
    }

    public ItemStack getItem(){
        return item;
    }

    public int getAmmo(){
        return ammo;
    }

    public int getMaxAmmo(){ return maxAmmo; }

    public void incrementAmmo(int increment){
        ammo = Math.min(maxAmmo, increment + ammo);
    }
    public void setForceReload(boolean forceReload){
        this.forceReload = forceReload;
    }

    public String getProjId(){
        return projId;
    }

    public String getId(){ return id; }

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
