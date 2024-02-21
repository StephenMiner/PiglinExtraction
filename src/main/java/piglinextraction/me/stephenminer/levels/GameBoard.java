package piglinextraction.me.stephenminer.levels;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.levels.objectives.Objective;
import piglinextraction.me.stephenminer.levels.objectives.ObjectiveType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GameBoard {
    private final PiglinExtraction plugin;
    private final List<UUID> players;
    private Scoreboard board;
    private HashMap<Team, Objective> teams;
    private Level level;
    public GameBoard(Level level){
        this.plugin = JavaPlugin.getPlugin(PiglinExtraction.class);
        this.level = level;
        this.players = new ArrayList<>();
        taken = new HashSet<>();
        teams = new HashMap<>();

    }



    public void initBoard(){
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        org.bukkit.scoreboard.Objective objective = board.registerNewObjective("board", Criteria.DUMMY,"Piglin Extraction");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        int i = 20;
        HashMap<ObjectiveType,List<Objective>> sorted = sortObjectives(level.getObjectives());
        for (ObjectiveType type : sorted.keySet()){
            objective.getScore(ChatColor.BOLD + type.collectionId()).setScore(i);
            i--;
            for (Objective obj : sorted.get(type)){
                Team team = board.registerNewTeam(obj.getDisplay());
                String entry = obj.getDisplay();//genUniqueColor();
                team.addEntry(entry);
                objective.getScore(entry).setScore(i);
                teams.put(team,obj);
                i--;
            }
        }

    }

    private HashMap<ObjectiveType,List<Objective>> sortObjectives(List<Objective> objectives){
        HashMap<ObjectiveType, List<Objective>> out = new HashMap<>();
        for (Objective obj : objectives){
            if (out.containsKey(obj.getType())) out.get(obj.getType()).add(obj);
            else {
                List<Objective> objs = new ArrayList<>();
                objs.add(obj);
                out.put(obj.getType(),objs);
            }
        }
        return out;
    }

    private Set<String> taken;

    private String genUniqueColor(){
        String out = validCombo();
        int attempts = 90;
        int tries = 0;
        while (taken.contains(out) && tries < attempts){
            out = validCombo();
            tries++;
        }
        taken.add(out);
        return out;
    }

    private String validCombo(){
        ChatColor[] colors = ChatColor.values();
        int i1 = ThreadLocalRandom.current().nextInt(colors.length);
        int i2 = ThreadLocalRandom.current().nextInt(colors.length);
        while ((colors[i1] != ChatColor.STRIKETHROUGH  && colors[i1] != ChatColor.RESET &&colors[i1] != ChatColor.BOLD && colors[i1] != ChatColor.ITALIC)
                && (colors[i2] != ChatColor.STRIKETHROUGH &&colors[i2] != ChatColor.RESET &&colors[i2] != ChatColor.BOLD && colors[i2] != ChatColor.ITALIC)){
            i1 = ThreadLocalRandom.current().nextInt(colors.length);
            i2 = ThreadLocalRandom.current().nextInt(colors.length);
        }
        return colors[i1] + "" + colors[i2];
    }




    public void update(){
        new BukkitRunnable(){
            @Override
            public void run(){
                if (!level.isStarted()){
                    for (int i = players.size()-1; i>=0; i--){
                        try{
                            Player player = Bukkit.getPlayer(players.get(i));
                            removePlayer(player);
                        }catch (Exception ignored){}
                    }
                    this.cancel();
                    return;
                }
                for (Team team : teams.keySet()){
                    team.setSuffix(teams.get(team).getStatus());
                }
            }
        }.runTaskTimer(plugin,1,1);
    }

    public void addPlayer(Player player){
        player.setScoreboard(board);
        players.add(player.getUniqueId());
    }


    public void removePlayer(Player player){
        players.remove(player.getUniqueId());
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

}
