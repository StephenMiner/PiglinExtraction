package piglinextraction.me.stephenminer.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GiveCmd implements CommandExecutor, TabCompleter {
    private final PiglinExtraction plugin;
    public GiveCmd(PiglinExtraction plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("pegive")){
            int size = args.length;
            if (size >= 2){
                try {
                    Player player = Bukkit.getPlayerExact(args[0]);
                    ItemStack give = fromId(args[1].toLowerCase());
                    player.getInventory().addItem(give);
                    player.sendMessage(ChatColor.GREEN + "Gave you " + args[1]);
                }catch (Exception e){
                    sender.sendMessage(ChatColor.RED + "Couldn't fin player " + args[0]);
                }
                return false;
            }
        }
        return false;
    }

    private ItemStack fromId(String id){
        Items items = new Items(plugin);
        id = id.toLowerCase();
        return switch (id){
            case "healingbrew" -> items.healingBrew(4);
            case "quiver" -> items.quiver(4);
            case "breaking" -> items.breaking();
            default -> new ItemStack(Material.STICK);
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("pegive")){
            int size = args.length;
            if (size == 2) return items(args[1]);
        }
        return null;
    }

    private List<String> filter(Collection<String> base, String match){
        match = match.toLowerCase();
        List<String> filtered = new ArrayList<>();
        for (String entry : base){
            String temp = entry.toLowerCase();
            if (temp.contains(match)) filtered.add(entry);
        }
        return filtered;
    }


    private List<String> items(String match){
        List<String> items = new ArrayList<>();
        items.add("healingbrew");
        items.add("quiver");
        items.add("breaking");
        return filter(items, match);
    }
}
