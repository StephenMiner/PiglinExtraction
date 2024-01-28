package piglinextraction.me.stephenminer.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.containers.Items;
import piglinextraction.me.stephenminer.containers.LockerGui;

import java.util.*;
import java.util.logging.Level;

public class LockerCmd implements CommandExecutor, TabCompleter {
    public static HashMap<UUID, LockerGui> cache = new HashMap<>();

    private final PiglinExtraction plugin;

    public LockerCmd(PiglinExtraction plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("locker"))
            if (sender instanceof Player player) {
                if (player.hasPermission("pe.commands.locker")) {
                    int size = args.length;
                    if (size == 2) {
                        if (args[0].equalsIgnoreCase("give")){
                            if (!lockerExists(args[1])){
                                player.sendMessage(ChatColor.RED + "Locker doesn't exist!");
                                return false;
                            }
                            Items items = new Items(plugin);
                            player.getInventory().addItem(items.lockerItem(args[1]));
                            player.sendMessage(ChatColor.GREEN + "Gave you your item!");
                            return true;
                        }
                    }
                    if (size == 3){
                        if (args[0].equalsIgnoreCase("edit")){
                            if (!lockerExists(args[1])){
                                player.sendMessage(ChatColor.RED + "Locker doesn't exist!");
                                return true;
                            }
                            LockerGui gui = new LockerGui(plugin, args[1]);
                            cache.put(player.getUniqueId(), gui);
                            player.openInventory(gui.lootTable(Integer.parseInt(args[2])));
                            player.sendMessage(ChatColor.GREEN + "Now editing locker " + args[1] + " loot-table #" + args[2]);
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("create")) {
                            if (lockerExists(args[1])) {
                                sender.sendMessage(ChatColor.RED + "The locker you are trying to create already exists! Use /locker edit [id] [loottable-#] to edit your locker!");
                                return false;
                            }
                            try {
                                Material mat = Material.matchMaterial(args[2]);
                                plugin.lockersFile.getConfig().set("lockers." + args[1] + ".material", mat.name());
                                plugin.lockersFile.saveConfig();
                                player.sendMessage(ChatColor.GREEN + "Created locker! Use /locker edit [id] [table #] to start creating loot-tables");
                                return true;
                            }catch (Exception e){
                                player.sendMessage(ChatColor.RED + "Material doesnt exist!");
                                plugin.getLogger().log(Level.WARNING, "Failed to parse material from command!");
                                plugin.getLogger().log(Level.INFO, e.getLocalizedMessage());
                            }

                        }
                    }
                } else sender.sendMessage(ChatColor.RED + "Sorry, but you do not have permission to use this command, contanct a server admin if you think this is a mistake!");
            } else sender.sendMessage(ChatColor.RED + "Sorry, but only players can use this command!");
        return false;
    }

    private boolean lockerExists(String id){
        return plugin.lockersFile.getConfig().contains("lockers." + id);
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("locker")){
            int size = args.length;
            if (size == 1) return subCmds();
            if (size == 2){
                if (args[0].equalsIgnoreCase("edit")) return lockers();
                if (args[0].equalsIgnoreCase("give")) return lockers();
            }
            if (size == 3){
                if (args[0].equalsIgnoreCase("edit")) return integer();
                if (args[0].equalsIgnoreCase("create")) return materials(args[2]);
            }
        }
        return null;
    }

    private List<String> subCmds(){
        List<String> list = new ArrayList<>();
        list.add("create");
        list.add("edit");
        list.add("give");
        return list;
    }
    private List<String> integer(){
        List<String> list = new ArrayList<>();
        list.add("[Integer]");
        return list;
    }
    private List<String> materials(String arg){
        List<String> list = new ArrayList<>();
        for (Material mat : Material.values()){
            String temp = mat.name().toLowerCase(Locale.ROOT);
            arg = arg.toLowerCase(Locale.ROOT);
            if (temp.contains(arg)) list.add(mat.name());
        }
        return list;
    }
    private List<String> lockers(){
        return new ArrayList<>(plugin.lockersFile.getConfig().getConfigurationSection("lockers").getKeys(false));
    }
}
