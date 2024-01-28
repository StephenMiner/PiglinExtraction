package piglinextraction.me.stephenminer.player.loadout;

import org.bukkit.entity.Player;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.weapons.mele.MeleWeapon;
import piglinextraction.me.stephenminer.weapons.mele.ShortSword;
import piglinextraction.me.stephenminer.weapons.mele.Spear;
import piglinextraction.me.stephenminer.weapons.mele.Warhammer;
import piglinextraction.me.stephenminer.weapons.ranged.LongRifle;
import piglinextraction.me.stephenminer.weapons.ranged.RangedWeapon;
import piglinextraction.me.stephenminer.weapons.ranged.RepeaterCrossbow;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadOut {
    public static List<LoadOut> loadouts = new ArrayList<>();

    private final PiglinExtraction plugin;
    private Player player;
    private String mele;
    private String primaryRanged;

    public LoadOut(PiglinExtraction plugin, Player player){
        this.plugin = plugin;
        this.player = player;
        loadouts.add(this);
    }

    public void createProfile(){
        mele = "spear";
        primaryRanged = "repeatercrossbow";
        save();
    }

    public boolean hasProfile(){
        return plugin.playersFile.getConfig().contains("players." + player.getUniqueId() + ".name");
    }

    public void load(){
        UUID uuid = player.getUniqueId();
        mele = plugin.playersFile.getConfig().getString("players." + uuid + ".mele");
        primaryRanged = plugin.playersFile.getConfig().getString("players." + uuid + ".primary-ranged");
    }
    public void save(){
        UUID uuid = player.getUniqueId();
        plugin.playersFile.getConfig().set("players." + uuid + ".name", player.getName());
        plugin.playersFile.getConfig().set("players." + uuid + ".mele", mele);
        plugin.playersFile.getConfig().set("players." + uuid + ".primary-ranged", primaryRanged);
        plugin.playersFile.saveConfig();
    }

    public void setMele(String weaponId){
        mele = weaponId;
        save();
    }
    public void setPrimaryRanged(String rangedId){
        primaryRanged = rangedId;
        save();
    }
    public String getMeleId(){
        return mele;
    }
    public String getPrimaryRangedId(){
        return primaryRanged;
    }

    public MeleWeapon getMeleWeapon(){
        MeleWeapon meleWeapon;
        switch (mele){
            case "spear" -> meleWeapon = new Spear(plugin, player);
            case "warhammer" -> meleWeapon = new Warhammer(plugin, player);
            case "shortsword" -> meleWeapon = new ShortSword(plugin, player);
            default ->  meleWeapon = new MeleWeapon(plugin, player);
        }
        plugin.meleWeapons.put(player.getUniqueId(), meleWeapon);
        return meleWeapon;
    }
    public RangedWeapon getPrimaryRanged(){
        RangedWeapon rangedWeapon;
        switch (primaryRanged){
            case "repeatercrossbow" -> rangedWeapon = new RepeaterCrossbow(plugin, player);
            case "longrifle" -> rangedWeapon = new LongRifle(plugin, player);
            default -> rangedWeapon = new RangedWeapon(plugin, player);
        }
        plugin.rangedWeaponsP.put(player.getUniqueId(), rangedWeapon);
        return rangedWeapon;
    }
    public Player getPlayer(){ return player; }

    public static LoadOut fromPlayer(Player player){
        for (LoadOut loadOut : LoadOut.loadouts){
            if (loadOut.getPlayer().getUniqueId().equals(player.getUniqueId())) return loadOut;
        }
        return null;
    }
}
