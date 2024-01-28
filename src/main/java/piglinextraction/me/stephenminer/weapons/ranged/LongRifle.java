package piglinextraction.me.stephenminer.weapons.ranged;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;

import java.util.ArrayList;
import java.util.List;

public class LongRifle extends RangedWeapon{

    public LongRifle(PiglinExtraction plugin, Player owner){
        super(plugin, owner);
        maxAmmo = 60;
        maxClip = 4;
        id = "longrifle";
        projId = "longrifle arrow";
        projDmg = 15;
        headshotMultiplier = 12;
        reloadtime = 120;
        itemslot = 2;
        shotInterval = 1;
        ammo = maxAmmo / 4;
        clip = maxClip;
        pierce = 2;
        item = longRifle();
        giveItem();
    }

    @Override
    protected void fire(){
        super.fire();
        if (cooldown > System.currentTimeMillis()) return;
        World world = owner.getWorld();
        Arrow arrow = owner.launchProjectile(Arrow.class);
        arrow.setCustomName(projId);
        arrow.setCustomNameVisible(false);
        arrow.setVelocity(arrow.getVelocity().multiply(2.5f));
        arrow.setPierceLevel(pierce);
        world.playSound(owner.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 2);
        cooldown = System.currentTimeMillis() + (int) (shotInterval * 1000);
    }

    public void zoom(boolean on) {
        if (on) {
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 5));
            owner.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0));
        }else{
            owner.removePotionEffect(PotionEffectType.SLOW);
            owner.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

    }

    public ItemStack longRifle(){
        ItemStack item = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Dwarven Long-Rifle");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A high damage rifle");
        lore.add(ChatColor.ITALIC + "left-click to reload");
        lore.add(ChatColor.ITALIC + "right-click to shoot");
        lore.add(ChatColor.ITALIC + "shift to zoom");
        lore.add(ChatColor.BLACK + id);
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        item.setItemMeta(meta);
        return item;
    }




}
