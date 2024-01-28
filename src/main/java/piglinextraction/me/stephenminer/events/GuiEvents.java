package piglinextraction.me.stephenminer.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.commands.LockerCmd;
import piglinextraction.me.stephenminer.containers.Items;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.LevelGroup;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.player.loadout.Selector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuiEvents implements Listener {
    private final PiglinExtraction plugin;
    private final List<Selector> selectors = new ArrayList<>();
    public GuiEvents(PiglinExtraction plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void invClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        String title = event.getView().getTitle();
        Inventory inv = event.getClickedInventory();
        if (item == null) return;
        if (item.getType().equals(Material.BARRIER)){
            player.closeInventory();
        }

        if (LockerCmd.cache.containsKey(player.getUniqueId()) &&
                LockerCmd.cache.get(player.getUniqueId()).getCurrentTitle()
                        .equalsIgnoreCase(event.getView().getTitle())){

        }
        if (title.equalsIgnoreCase("Levels")){
            event.setCancelled(true);
            Items items = new Items(plugin);
            LevelGroup fromItem = null;
            for (LevelGroup levelGroup : LevelGroup.levelGroups){
                if (items.hasLore(item, levelGroup.getName())) fromItem = levelGroup;
            }
            if (fromItem == null) return;
            player.closeInventory();
            Bukkit.dispatchCommand(player, "join " + fromItem.getName());
            return;
        }

        if (title.equals("Expedition Gear")){
            event.setCancelled(true);
            for (int i = selectors.size() - 1; i >= 0; i--){
                Selector selector = selectors.get(i);
                if (selector.getPlayer().equals(player)){
                    selector.onClick(player, event.getSlot());
                    return;
                }
            }
        }
    }
    @EventHandler
    public void listCleanUp(PlayerQuitEvent event){
        Player player = event.getPlayer();
        for (int i = selectors.size()-1; i >= 0; i--){
            Selector selector = selectors.get(i);
            if (selector.getPlayer().equals(player)){
                selectors.remove(i);
                return;
            }
        }
    }
    @EventHandler
    public void invClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        if(LockerCmd.cache.containsKey(player.getUniqueId())){
            LockerCmd.cache.get(player.getUniqueId()).save();
            LockerCmd.cache.remove(player.getUniqueId());
        }
    }
    @EventHandler
    public void invOpen(InventoryOpenEvent event){
        Player player = (Player) event.getPlayer();
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (title.equalsIgnoreCase("Levels")){
            for (LevelGroup group : LevelGroup.levelGroups){
                ItemStack item = group.asItem();
                event.getInventory().addItem(item);
            }
            event.getInventory().setItem(event.getInventory().getSize()-1, close());
        }
        for (LevelGroup group : LevelGroup.levelGroups){
            if (title.contains(group.getName())){
                Items items = new Items(plugin);
                Inventory inv = event.getInventory();
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        if (!inv.getViewers().isEmpty()) {
                            for (int i = 0; i < group.levelNames().size(); i++) {
                                if (i < inv.getSize()) {
                                    inv.setItem(i, items.levelIcon(group.levelNames().get(i)));
                                }
                            }
                        }
                    }
                }.runTaskTimer(plugin, 2, 1);
            }
        }
    }

    @EventHandler
    public void menuClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        String title = ChatColor.stripColor(event.getView().getTitle());
        for (LevelGroup group : LevelGroup.levelGroups){
            if (title.contains(group.getName())){
                event.setCancelled(true);
                if (event.getCurrentItem() == null) return;
                ItemStack item = event.getCurrentItem();
                String level = fromIcon(item);
                if (level == null) return;
                player.closeInventory();
                Bukkit.dispatchCommand(player, "join " + group.getName() + " " + level);
            }
        }
    }

    @EventHandler
    public void onAnimate(PlayerAnimationEvent event){
        Player player = event.getPlayer();
        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING){
            if (player.getInventory().getItemInMainHand() != null){
                for (Level level : Level.levels){
                    if (level.getWaitingRoom().hasPlayer(player)){
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (item.getType() == Material.LIME_DYE){
                            if (player.getCooldown(Material.LIME_DYE) > 0 ) return;
                            player.setCooldown(Material.RED_DYE, 20);
                            level.getWaitingRoom().readyPlayer(player, true);
                            event.setCancelled(true);
                            return;
                        }
                        if (item.getType() == Material.RED_DYE){
                            if (player.getCooldown(Material.RED_DYE) > 0) return;
                            player.setCooldown(Material.LIME_DYE, 20);
                            level.getWaitingRoom().readyPlayer(player, false);
                            event.setCancelled(true);
                            return;
                        }
                        if (item.getType() == Material.CHEST){
                            Selector selector = new Selector(plugin, player);
                            selector.load();
                            player.openInventory(selector.getDisplay());
                            selectors.add(selector);
                        }
                    }
                }
            }
        }
    }

    private Material getIcon(String levelId){
        if (plugin.levelsFile.getConfig().contains("levels." + levelId + ".mat")){
            Material icon = Material.matchMaterial(plugin.levelsFile.getConfig().getString("levels" + levelId + ".mat"));
            return icon != null ? icon : Material.DIRT;
        }
        return Material.DIRT;
    }

    private ItemStack close(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close Menu");
        item.setItemMeta(meta);
        return item;
    }

    private boolean hasLore(ItemStack item, String check){
        if (item.hasItemMeta() && item.getItemMeta().hasLore()){
            List<String> lore = item.getItemMeta().getLore();
            for (String entry : lore){
                String temp = ChatColor.stripColor(entry);
                if (temp.equalsIgnoreCase(check)) return true;
            }
        }
        return false;
    }

    private String fromIcon(ItemStack item){
        Set<String> ids = plugin.levelsFile.getConfig().getConfigurationSection("levels").getKeys(false);
        for (String roomId : ids){
            if (hasLore(item, roomId)) return roomId;
        }
        return null;
    }
}
