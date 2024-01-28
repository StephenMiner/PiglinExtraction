package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Items;

public class RoomWand implements CommandExecutor {
    private final PiglinExtraction plugin;
    public RoomWand(PiglinExtraction plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]){
        if (cmd.getName().equalsIgnoreCase("roomWand")){
            if (sender instanceof Player player){
                if (player.hasPermission("pe.commands.wand")){
                    Items items = new Items(plugin);
                    int size = args.length;
                    if (size < 1)
                        player.getInventory().addItem(items.roomWand());
                    else{
                        if (args[0].equals("door")){
                            player.getInventory().addItem(items.doorWand());
                        }
                    }
                }else player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            }else sender.sendMessage(ChatColor.RED + "Sorry! This command involves giving an entity an item so non-players can't use this command!");
            return false;
        }
        return false;
    }
}
