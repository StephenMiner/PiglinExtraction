package piglinextraction.me.stephenminer.weapons.mele;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.weapons.ArmorPiercing;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShortSword extends MeleWeapon {

    public ShortSword(PiglinExtraction plugin, Player owner){
        super(plugin, owner);
        this.id = "shortsword";
        this.damage = 5;
        this.headsotMultiplier = 5.3f;
        this.attackSpeed = -2.6;
        this.range = 2;
        hitStagger = 25;
        chargeStagger = true;
        armorPierce = ArmorPiercing.LIGHT;
        this.item = blade();
        giveItem();
        trackAttackCooldown();
    }


    public ItemStack blade(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Mythril Sword");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.ITALIC + "A Fast Paced Weapon");
        lore.add(ChatColor.ITALIC + "Fast attack speed");
        lore.add(ChatColor.ITALIC + "Bonus headshot dmg");
        lore.add("");
        lore.add(ChatColor.GRAY + "When in Main Hand:");
        lore.add(ChatColor.DARK_GREEN + "" + damage + " attack damage");
        lore.add(ChatColor.DARK_GREEN + "" + getRealSpeed() + " attack speed");
        lore.add(ChatColor.DARK_GREEN + "" + range + " Attack Range");
        lore.add(ChatColor.BLACK + "shortsword");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        AttributeModifier AttackDamage = new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", dummyDmg, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        AttributeModifier AttackSpeed = new AttributeModifier(UUID.randomUUID(), "generic.attackSpeed", attackSpeed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttackDamage);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, AttackSpeed);
        item.setItemMeta(meta);
        return item;
    }
}
