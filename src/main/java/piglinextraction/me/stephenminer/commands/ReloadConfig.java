package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import piglinextraction.me.stephenminer.PiglinExtraction;

public class ReloadConfig implements CommandExecutor {
    private final PiglinExtraction plugin;

    public ReloadConfig(){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player player){
            if (!player.hasPermission("pe.commands.reload")){
                player.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                return false;
            }
        }
        plugin.roomsFile.reloadConfig();
        plugin.levelsFile.reloadConfig();
        plugin.lockersFile.reloadConfig();
        plugin.hordesFile.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Reloaded all config files");
        return true;
    }
}
