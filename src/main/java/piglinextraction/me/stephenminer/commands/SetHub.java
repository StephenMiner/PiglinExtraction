package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;

public class SetHub implements CommandExecutor {
    private final PiglinExtraction plugin;

    public SetHub(PiglinExtraction plugin){
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("pe.commands.sethub")){
                player.sendMessage(ChatColor.RED + "Drip check failed");
                return false;
            }
            plugin.hub = player.getLocation();
            plugin.worldsFile.getConfig().set("hub", plugin.fromLoc(player.getLocation()));
            plugin.worldsFile.saveConfig();
            player.sendMessage(ChatColor.GREEN + "Set hub location as your current location!");
            return true;
        }else sender.sendMessage(ChatColor.RED + "Sorry, but only players can use this command!");
        return false;
    }
}
