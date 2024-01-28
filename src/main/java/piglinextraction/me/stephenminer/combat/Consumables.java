package piglinextraction.me.stephenminer.combat;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.events.custom.PlayerNoiseEvent;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;

import java.util.List;

public class Consumables implements Listener {
    private final PiglinExtraction plugin;
    public Consumables(PiglinExtraction plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void useHealthPack(PlayerInteractEvent event){
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (hasLore(item, "healingbrew")){
            Action action = event.getAction();
            if (action.isLeftClick()){
                if (player.getHealth() >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    player.sendMessage(ChatColor.AQUA + "You are already at max health!");
                    return;
                }
                int uses = parseUses(item, "Uses: ");
                if (uses - 1 <= 0) item.setAmount(0);
                else setUses(item, uses - 1);
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
                World world = player.getWorld();
                world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1, 1);
                world.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                Bukkit.getServer().getPluginManager().callEvent(new PlayerNoiseEvent(player, 2));
            }
            if (action.isRightClick()){
                int uses = parseUses(item, "Uses: ");
                boolean trace = traceLocation(player, Particle.HEART, 5, true);
                if (trace){
                    if (uses - 1 <= 0) item.setAmount(0);
                    else setUses(item, uses - 1);
                }
            }
        }
    }

    @EventHandler
    public void useQuiver(PlayerInteractEvent event){
        if (!event.hasItem()) return;
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (hasLore(item, "quiver")) {
            int uses = parseUses(item, "Uses: ");
            Action action = event.getAction();
            if (action.isLeftClick()) {
                if (plugin.rangedWeaponsP.containsKey(player.getUniqueId())) {
                    if (uses - 1 <= 0) item.setAmount(0);
                    else setUses(item, uses - 1);
                    RangedWeapon weapon = plugin.rangedWeaponsP.get(player.getUniqueId());
                    weapon.setAmmo(weapon.getAmmo() + weapon.getMaxAmmo()/4);
                }
            }
            if (action.isRightClick()){
                boolean trace = traceLocation(player, Particle.CRIT, 5, false);
                if (trace){
                    if (uses - 1 <= 0) item.setAmount(0);
                    else setUses(item, uses - 1);
                }
            }
        }
    }




    private int parseUses(ItemStack item, String target){
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return 0;
        List<String> lore = item.getItemMeta().getLore();
        target = target.toLowerCase();
        for (String entry : lore){
            String temp = ChatColor.stripColor(entry).toLowerCase();
            if (temp.contains(target)){
                int parsed = Integer.parseInt(temp.replace(target, ""));
                return parsed;
            }
        }
        return 0;
    }

    private void setUses(ItemStack item, int uses){
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return;
        List<String> lore = item.getItemMeta().getLore();
        for (int i = 0; i < lore.size(); i++){
            String entry = lore.get(i);
            String temp = ChatColor.stripColor(entry).toLowerCase();
            if (temp.contains("uses: ")){
                String replaceWith = temp.substring(0, temp.indexOf(" ") + 1) + uses;
                entry = entry.replace(entry, replaceWith);
                lore.set(i, ChatColor.YELLOW + entry);
            }
        }
        String displayName = item.getItemMeta().getDisplayName();
        String name = displayName.substring(0, displayName.indexOf("(")) + "(" + uses + " uses)";
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    private boolean hasLore(ItemStack item, String target){
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        List<String> lore = item.getItemMeta().getLore();
        target = target.toLowerCase();
        for (String entry : lore){
            String temp = ChatColor.stripColor(entry).toLowerCase();
            if (temp.contains(target)) return true;
        }
        return false;
    }

    private boolean traceLocation(LivingEntity source, Particle particle, int range, boolean heal){
        World world = source.getWorld();
        Location loc = source.getLocation();
        Vector dir = source.getLocation().getDirection();
        for (float i = 0; i < range; i+= 0.2f){
            loc.add(dir);
            loc.add(0,1.5,0);
            world.spawnParticle(particle, loc, 0);
            if (!loc.getBlock().getType().isAir() && !loc.getBlock().isPassable() && !loc.getBlock().isLiquid()) return false;
            for (Entity entity : world.getNearbyEntities(loc, 1.5,1.5, 1.5)){
                if (entity.equals(source)) continue;
                if (entity instanceof Player player){
                    Vector min = new Vector(loc.getX()-0.25, loc.getY()-0.25, loc.getZ()-0.25);
                    Vector max = new Vector(loc.getX() + 0.25, loc.getY() + 0.25, loc.getZ() + 0.25);
                    if (player.getBoundingBox().overlaps(BoundingBox.of(min, max))){
                        if (heal){
                            Bukkit.broadcastMessage("" + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                            if (player.getHealth() >= player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) continue;
                        }else{
                            if (plugin.rangedWeaponsP.containsKey(player.getUniqueId())){
                                RangedWeapon weapon = plugin.rangedWeaponsP.get(player.getUniqueId());
                                if (weapon.getAmmo() >= weapon.getMaxAmmo()) continue;
                            }
                        }
                        player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 1, 1));
                        return true;
                    }
                }
            }
            loc.subtract(0,1.5,0);
        }
        return false;
    }
}
