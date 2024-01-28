package piglinextraction.me.stephenminer.player.loadout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.player.DisplayItems;

import java.util.logging.Level;

public class Selector {
    private final PiglinExtraction plugin;

    private final String shortSword = "shortsword";
    private final String spear = "spear";
    private final String warhammer = "warhammer";

    private final String longRifle = "longrifle";
    private final String repeater = "repeatercrossbow";

    private Player player;
    private Inventory display;

    public Selector(PiglinExtraction plugin, Player player){
        this.player = player;
        this.plugin = plugin;
    }



    public void load(){
        DisplayItems displayItems = new DisplayItems();
        Inventory inv = Bukkit.createInventory(null, 54, "Expedition Gear");
        for (int i = 0; i < inv.getSize(); i++){
            inv.setItem(i, filler());
        }
        inv.setItem(10, meleClass());
        inv.setItem(11, displayItems.displayWarhammer());
        inv.setItem(12, displayItems.displaySpear());
        inv.setItem(13, displayItems.displayShortSword());

        inv.setItem(19, primaryRangedClass());
        inv.setItem(20, displayItems.displayRepeater());
        inv.setItem(21, displayItems.displayLongRifle());
        inv.setItem(49, exit());
        display = inv;
        updateInv();
    }

    public void updateInv(){
        display.setItem(37, yourGear());
        LoadOut loadOut = LoadOut.fromPlayer(player);
        if (loadOut == null){
            player.sendMessage("Your loadout is null, something went wrong");
            return;
        }
        display.setItem(38, getDisplayItem(loadOut.getMeleId()));
        display.setItem(39, getDisplayItem(loadOut.getPrimaryRangedId()));
        //player.updateInventory();
    }

    public ItemStack getDisplayItem(String str){
        DisplayItems displayItems = new DisplayItems();
        return switch (str){
            case warhammer -> displayItems.displayWarhammer();
            case spear -> displayItems.displaySpear();
            case shortSword -> displayItems.displayShortSword();
            case repeater -> displayItems.displayRepeater();
            case longRifle -> displayItems.displayLongRifle();
            default -> displayItems.selectorItem();
        };
    }

    public Player getPlayer(){
        return player;
    }

    public void onClick(Player player,  int clickedSlot){
        LoadOut loadOut = LoadOut.fromPlayer(player);
        if (loadOut == null){
            plugin.getLogger().log(Level.WARNING, "Player " + player.getName() + " does not have a loadout");
            return;
        }
        switch (clickedSlot){
            case 11 -> loadOut.setMele(warhammer);
            case 12 -> loadOut.setMele(spear);
            case 13 -> loadOut.setMele(shortSword);

            case 20 -> loadOut.setPrimaryRanged(repeater);
            case 21 -> loadOut.setPrimaryRanged(longRifle);
        }
        updateInv();
    }

    private ItemStack filler(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack meleClass(){
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Mele Weapons");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack exit(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Close Menu");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack primaryRangedClass(){
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Primary Ranged Weapons");
        item.setItemMeta(meta);
        return item;
    }
    private ItemStack yourGear(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Your loadout");
        item.setItemMeta(meta);
        return item;
    }

    public Inventory getDisplay(){ return display; }


}
