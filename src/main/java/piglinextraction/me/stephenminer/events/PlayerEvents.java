package piglinextraction.me.stephenminer.events;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.Level;

import org.bukkit.event.Listener;
import piglinextraction.me.stephenminer.player.DeathMarker;
import piglinextraction.me.stephenminer.player.GameProfile;
import piglinextraction.me.stephenminer.player.loadout.LoadOut;
import piglinextraction.me.stephenminer.weapons.Flashlight;
import piglinextraction.me.stephenminer.weapons.Shield;
import piglinextraction.me.stephenminer.weapons.ranged.LongRifle;


import java.util.List;
import java.util.UUID;
import java.util.HashMap;

public class PlayerEvents implements Listener{
    private final PiglinExtraction plugin;
    private HashMap<UUID, ItemStack[]> offlineItems;
    private HashMap<UUID, Location>  offlineLocs;
    public PlayerEvents(PiglinExtraction plugin){
        this.plugin = plugin;
        offlineItems = new HashMap<>();
        offlineLocs = new HashMap<>();
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        Location logout = player.getLocation();
        ItemStack[] contents = player.getInventory().getContents();
        int start = plugin.profiles.size() - 1;
        for (int i = start; i >= 0; i--){
            GameProfile profile = plugin.profiles.get(i);
            if (profile.getPlayer().getUniqueId().equals(player.getUniqueId())){
                plugin.profiles.remove(i);
            }
        }
        for (int i = LoadOut.loadouts.size() - 1; i >= 0; i--){
            LoadOut loadOut = LoadOut.loadouts.get(i);
            if (loadOut.getPlayer().equals(player)){
                LoadOut.loadouts.remove(i);
                break;
            }
        }
        for (Level level : Level.levels){
            if (level.getWaitingRoom().hasPlayer(player)){
                level.getWaitingRoom().removePlayer(player);
            }
            for (int i = level.getPlayers().size() - 1; i >= 0; i--){
                UUID uuid = level.getPlayers().get(i);
                if(player.getUniqueId().equals(uuid)){
                    level.getOffline().add(uuid);
                    offlineItems.put(uuid, contents);
                    offlineLocs.put(uuid, logout);
                    level.getPlayers().remove(uuid);
                    for (UUID id : level.getPlayers()){
                        Player p = Bukkit.getPlayer(id);
                        p.sendMessage(ChatColor.RED + player.getName() + " has lost contact with the group");
                    }
                    checkRelog(level, player.getUniqueId());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        GameProfile profile = new GameProfile(plugin, player);
        plugin.profiles.add(profile);
        player.teleport (plugin.hub);
        for (Level level : Level.levels){
            for (int i = level.getOffline().size() - 1; i >= 0; i--){
                UUID uuid = level.getOffline().get(i);
                if (player.getUniqueId().equals(uuid)){
                    level.getOffline().remove(uuid);
                    if (!level.getDead().contains(uuid))
                        level.getPlayers().add(uuid);
                    player.getInventory().setContents(offlineItems.get(uuid));
                    player.teleport(offlineLocs.get(uuid));
                    offlineItems.remove(uuid);
                    offlineLocs.remove(uuid);
                    for (UUID id : level.getPlayers()){
                        Player p = Bukkit.getPlayer(id);
                        p.sendMessage(ChatColor.GREEN + player.getName() + " has returned to the expedition");
                    }
                }
            }
        }
        LoadOut loadOut = new LoadOut(plugin, player);
        if (loadOut.hasProfile()){
            loadOut.load();
        }else{
            loadOut.createProfile();
            loadOut.save();
        }
    }

    @EventHandler
    public void isDead(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player player){
            if (isDead(player)) event.setCancelled(true);
        }
        if (event.getDamager() instanceof Projectile projectile){
            if (projectile.getShooter() instanceof Player player){
                if (isDead(player)) event.setCancelled(true);
            }
        }
    }

    public boolean isDead(Player player){
        for (Level level : Level.levels){
            if (level.getDead().contains(player.getUniqueId())) return true;
        }
        return false;
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event){
        if (event.getTarget() instanceof Player player){
            if (isDead(player)){
                event.setCancelled(true);
            }
        }
    }
    /*
    @EventHandler
    public void onSpawn(EntityAddToWorldEvent event){
        if (event.getEntity() instanceof Piglin piglin){
            for (PiglinGrunt grunt : PiglinGrunt.cache){
                if (grunt.entityIsSimilar(piglin)){
                    Bukkit.getMobGoals().rem
                }
            }
        }
    }

     */
    public void clearOfTarget(Player player){
        for (Entity entity : player.getWorld().getEntities()){
            if (entity instanceof Mob mob){
                mob.setTarget(null);
            }
        }
    }
    @EventHandler
    public void checkDeath(EntityDamageEvent event){
        Location loc = event.getEntity().getLocation();
        if (event.getEntity() instanceof Player player){
            for (Level level : Level.levels){
                if (level.getDead().contains(player.getUniqueId())){
                    event.setCancelled(true);
                }
            }
            if (player.getHealth() - event.getFinalDamage() <= 0){
                for (Level level : Level.levels) {
                    if (level.getDead().contains(player.getUniqueId())){
                        event.setCancelled(true);
                    }
                    if (level.getPlayers().contains(player.getUniqueId())) {
                        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)){
                            event.setCancelled(true);
                            clearOfTarget(player);
                            return;
                        }
                        if (plugin.flashlights.containsKey(player.getUniqueId())){
                            plugin.flashlights.get(player.getUniqueId()).toggle(false);
                        }
                        if (plugin.rangedWeaponsP.containsKey(player.getUniqueId())){
                            plugin.rangedWeaponsP.get(player.getUniqueId()).setForceReload(true);
                        }
                        level.getStats().addDowned(player.getUniqueId(), 1);
                        clearOfTarget(player);
                        event.setCancelled(true);
                        level.getDead().add(player.getUniqueId());
                        level.getPlayers().remove(player.getUniqueId());
                        DeathMarker marker = new DeathMarker(plugin, level, player, loc);
                        player.setHealth(10);
                        player.setGameMode(GameMode.SPECTATOR);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 0));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (level.isStarted() && level.getDead().contains(player.getUniqueId())) {
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        p.hidePlayer(plugin, player);
                                    }
                                }else{
                                    for (Player p : Bukkit.getOnlinePlayers()){
                                        p.showPlayer(plugin, player);
                                        p.removePotionEffect(PotionEffectType.INVISIBILITY);
                                    }
                                    player.setGameMode(GameMode.SURVIVAL);
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 5, 1);
                    }
                }

            }
        }
    }

    @EventHandler
    public void shield(EntityDamageEvent event){
        if (event.getEntity() instanceof Player player){
            ItemStack offhand = player.getInventory().getItemInOffHand();
            UUID uuid = player.getUniqueId();
            if (player.isHandRaised() && offhand.getType() == Material.SHIELD && Shield.shields.containsKey(uuid)){
                Shield shield = Shield.shields.get(uuid);
                shield.block(event);
            }
        }
    }

    @EventHandler
    public void trackKills(EntityDamageByEntityEvent event){
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Monster monster){
            if (monster.getHealth() - event.getFinalDamage() <= 0) {
                if (event.getDamager() instanceof Player player) {
                    addKill(player);
                    return;
                }
            }
        }
    }

    private void addKill(Player player){
        for (Level level : Level.levels){
            if (level.getPlayers().contains(player.getUniqueId())){
                player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "+1 kill");
                level.getStats().addKill(player.getUniqueId(), 1);
                for (GameProfile profile : plugin.profiles){
                    if (profile.getPlayer().equals(player)){
                        profile.incrementKills(1);
                        break;
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        for (Level level : Level.levels){
            if (level.getDead().contains(player.getUniqueId())){
                Location to = event.getTo();
                Location from = event.getFrom();
                if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onRevive(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (event.isSneaking()){
            World world = player.getWorld();
            for (Level level : Level.levels) {
                if (level.getPlayers().contains(player.getUniqueId())) {
                    for (Entity entity : world.getNearbyEntities(player.getLocation(), 2, 2, 2)) {
                        if (entity instanceof ArmorStand armorStand) {
                            for (DeathMarker marker : plugin.markers){
                                if (marker.getArmorStand().equals(armorStand)){
                                    OfflinePlayer p = marker.getPlayer();
                                    if (!p.isOnline() || p.equals(player) || !level.getDead().contains(p.getUniqueId())) continue;
                                    startRevival(player, p.getPlayer());
                                    return;
                                }
                            }

                        }
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void toggleFlashlight(PlayerInteractEvent event){
        if (!event.hasItem()) return;
        Action action = event.getAction();
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (action.isRightClick()){
            if (player.hasCooldown(Material.SPYGLASS)) return;
            if (plugin.flashlights.containsKey(player.getUniqueId())){
                if (hasLore(item, "flashlight") && item.getType() == Material.SPYGLASS) {
                    Flashlight flashlight = plugin.flashlights.get(player.getUniqueId());
                    boolean newToggle = !flashlight.isToggled();
                    if (newToggle) {
                        flashlight.toggle(true);
                        flashlight.turnOn();
                        player.sendMessage(ChatColor.GREEN + "Activated flashlight!");
                    } else {
                        flashlight.toggle(false);
                        player.sendMessage(ChatColor.RED + "De-Activated flashlight!");
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_FLINTANDSTEEL_USE, 1, 1);
                    player.setCooldown(Material.SPYGLASS, 10);
                    event.setCancelled(true);
                }

            }
        }
    }
    @EventHandler
    public void handleZoom(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) return;
        if (player.getInventory().getItemInMainHand() == null) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (plugin.rangedWeaponsP.containsKey(player.getUniqueId()) && plugin.rangedWeaponsP.get(player.getUniqueId()) instanceof LongRifle rifle) {
            if (hasLore(item, rifle.getId())){
                rifle.zoom(event.isSneaking());
            }
        }
    }

    private void startRevival(Player reviving, Player dead){
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (!dead.isOnline() || !reviving.isOnline()){
                    this.cancel();
                    return;
                }
                if(!reviving.isSneaking()){
                    this.cancel();
                    return;
                }
                for (Level level : Level.levels){
                    if (level.getPlayers().contains(reviving.getUniqueId()) && level.getDead().contains(dead.getUniqueId())){
                        if (count % 20 == 0) {
                            reviving.sendActionBar(ChatColor.AQUA + "Reviving " + dead.getName() + ": " + (100- count) / 20 + " seconds remaining");
                            dead.sendActionBar(ChatColor.AQUA + "Being revived by " + reviving.getName() + ": " + count / 20 + " seconds remaining");
                        }
                        if (count == 100){
                            level.revivePlayer(dead);
                            for (GameProfile profile : plugin.profiles){
                                if (profile.getPlayer().equals(reviving)) {
                                    profile.incrementRevives(1);
                                    profile.saveProfile();
                                    break;
                                }
                            }
                            level.getStats().addRevived(reviving.getUniqueId(), 1);
                            for (int i = plugin.markers.size(); i >= 0; i--){
                                DeathMarker marker = plugin.markers.get(i);
                                if (marker.getPlayer().getUniqueId().equals(dead.getUniqueId())) plugin.markers.remove(i);
                            }
                            this.cancel();
                            return;
                        }
                        count++;
                    }

                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }





    private void checkRelog(Level level, UUID uuid){
        new BukkitRunnable(){
            private int time = 1200;
            @Override
            public void run(){
                if (!level.isStarted()){
                    offlineItems.clear();
                    offlineLocs.clear();
                    level.getOffline().clear();
                    this.cancel();
                    return;
                }
                if (level.getPlayers().contains(uuid)){
                    this.cancel();
                    return;
                }
                if (time <= 0){
                    offlineLocs.remove(uuid);
                    offlineItems.remove(uuid);
                    level.getOffline().remove(uuid);
                    level.getDead().remove(uuid);
                    for (UUID id : level.getPlayers()){
                        Player p = Bukkit.getPlayer(id);
                        p.sendMessage(ChatColor.RED + Bukkit.getPlayer(uuid).getName() + " has been to lost to the darkness");
                    }
                    clearEquipment(uuid);
                    this.cancel();
                    return;
                }
                time--;
            }
        }.runTaskTimer(plugin, 5, 1);
    }
    private void clearEquipment(UUID uuid){
        plugin.flashlights.remove(uuid);
        plugin.meleWeapons.remove(uuid);
        plugin.rangedWeaponsP.remove(uuid);
    }
    private boolean hasLore(ItemStack item, String target){
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return false;
        target = target.toLowerCase();
        List<String> lore = item.getItemMeta().getLore();
        for (String entry : lore){
            String temp = ChatColor.stripColor(entry).toLowerCase();
            if (temp.equals(target)) return true;
        }
        return false;
    }



}
