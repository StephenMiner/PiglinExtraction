package piglinextraction.me.stephenminer.weapons.ranged;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;

import java.util.ArrayList;
import java.util.List;

public class RepeaterCrossbow extends RangedWeapon {


    public RepeaterCrossbow(PiglinExtraction plugin, Player player){
        super(plugin, player);
        id = "repeatercrossbow";
        projId = "repeater crossbow arrow";

        item = repeaterCrossbow();
        shotInterval = 0.1;
        maxAmmo = 320;
        maxClip = 25;
        clip = maxClip;
        ammo = maxAmmo / 4;
        reloadtime = 60;
        itemslot = 2;
        projDmg = 7;
        headshotMultiplier = 4.5f;
        pierce = 0;
        giveItem();
        armorPierce = ArmorPiercing.MEDIUM;

    }



    @Override
    protected void fire(){
        if (cooldown > System.currentTimeMillis()) return;
        World world = owner.getWorld();
        super.fire();
        Arrow arrow = owner.launchProjectile(Arrow.class);
        arrow.setCustomName(projId);
        arrow.setCustomNameVisible(false);
        arrow.setPierceLevel(pierce);
        world.playSound(owner.getLocation(), Sound.BLOCK_PISTON_EXTEND, 2, 2);
        cooldown = System.currentTimeMillis() + (int) (shotInterval * 1000);
    }








    public ItemStack repeaterCrossbow(){
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Repeater Crossbow");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A rapid fire crossbow");
        lore.add(ChatColor.ITALIC + "left-click to reload");
        lore.add(ChatColor.ITALIC + "right-click to shoot");
        lore.add(ChatColor.BLACK + id);
        meta.setLore(lore);
        meta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        item.setItemMeta(meta);
        return item;
    }



}
