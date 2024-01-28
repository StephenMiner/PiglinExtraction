package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.PiglinGrunt;
import piglinextraction.me.stephenminer.mobs.PiglinKnight;
import piglinextraction.me.stephenminer.weapons.Flashlight;
import piglinextraction.me.stephenminer.weapons.Shield;
import piglinextraction.me.stephenminer.weapons.mele.Spear;
import piglinextraction.me.stephenminer.weapons.mele.Warhammer;
import piglinextraction.me.stephenminer.weapons.ranged.LongRifle;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;
import piglinextraction.me.stephenminer.weapons.ranged.RepeaterCrossbow;
import piglinextraction.me.stephenminer.weapons.mele.ShortSword;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Testing implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;

    public Testing(PiglinExtraction plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("testing")){
            if (sender instanceof Player player){
                int size = args.length;
                if (size == 1){
                    String sub = args[0];
                    sub = sub.toLowerCase(Locale.ROOT);
                    switch (sub) {
                        case "knife" -> plugin.meleWeapons.put(player.getUniqueId(), new ShortSword(plugin, player));
                        case "hammer" -> plugin.meleWeapons.put(player.getUniqueId(), new Warhammer(plugin, player));
                        case "spear" -> plugin.meleWeapons.put(player.getUniqueId(), new Spear(plugin, player));
                        case "repeater" -> plugin.rangedWeaponsP.put(player.getUniqueId(), new RepeaterCrossbow(plugin, player));
                        case "longrifle" -> plugin.rangedWeaponsP.put(player.getUniqueId(), new LongRifle(plugin, player));
                        case "flashlight" -> plugin.flashlights.put(player.getUniqueId(), new Flashlight(plugin, player, 6, 1, 6));
                        case "restock" -> restockAmmo(player.getUniqueId());
                        case "shield"-> {
                            Shield shield = new Shield();
                            player.getInventory().setItemInOffHand(shield.getItem());
                            Shield.shields.put(player.getUniqueId(),shield);

                        }
                    }
                    player.sendMessage(ChatColor.GREEN + "Adding items to your inventory");
                    return true;
                }
                if (size == 2){
                    String sub = args[0];
                    if (sub.equalsIgnoreCase("spawn")) {
                        String mob = args[1];
                        switch (mob){
                            case "grunt" -> new PiglinGrunt(plugin, player.getLocation());
                            case "knight" -> new PiglinKnight(plugin,player.getLocation() );
                        }
                    }
                }
            }
        }
        return false;
    }

    private void restockAmmo(UUID uuid){
        if (plugin.rangedWeaponsP.containsKey(uuid)){
            RangedWeapon weapon = plugin.rangedWeaponsP.get(uuid);
            weapon.setAmmo(weapon.getAmmo() + 100);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        int size = args.length;
        if (cmd.getName().equalsIgnoreCase("testing")){
            if (size == 1) return weaponsList(args[0]);
        }
        return null;
    }

    private List<String> weaponsList(String match){
        List<String> weapons = new ArrayList<>();
        weapons.add("knife");
        weapons.add("hammer");
        weapons.add("spear");
        weapons.add("repeater");
        weapons.add("longrifle");
        weapons.add("flashlight");
        weapons.add("restock");
        List<String> matched = new ArrayList<>();
        match = match.toLowerCase(Locale.ROOT);
        for (String entry : weapons){
            entry = entry.toLowerCase(Locale.ROOT);
            if (entry.contains(match)) matched.add(entry);
        }
        return matched;
    }
}
