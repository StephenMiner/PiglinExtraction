package piglinextraction.me.stephenminer.mobs.boss.encounters;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import piglinextraction.me.stephenminer.levels.Level;
import piglinextraction.me.stephenminer.levels.builders.HordeBuilder;
import piglinextraction.me.stephenminer.levels.rooms.Door;
import piglinextraction.me.stephenminer.levels.rooms.Room;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;
import piglinextraction.me.stephenminer.mobs.hordes.SpawnNode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class HordeRoom extends RoomEncounter{
    private List<Horde> hordes;
    private boolean canTrigger,openDoors;
    /**
     * Time (ticks) for horde to spawn again should players be present in the room
     */
    private int cooldown;

    /**
     * @param str formatted as "horderoom=room=flag=open-doors=cooldown=horde1%horde2%horde3"
     * Cooldown should be -1 if you don't wish this to be repeatable.
     * Also note that cooldown should be written as if it were seconds, it will be converted into ticks for this class.
     */
    public HordeRoom(String str) {
        super("horderoom");
        canTrigger = true;
        loadBasicData(str);
    }

    /**
     * @param str formatted as "horderoom=room=flag=open-doors=cooldown=horde1%horde2%horde3%..."
     * Note that open-doors should be true/false value. If true, when horde is triggered,
     * doors associated with the room will open
     */
    private void loadBasicData(String str){
        String[] split = str.split("=");
        this.room = Room.BY_IDS.getOrDefault(split[1], null);
        this.flag = Flag.valueOf(split[2]);
        this.openDoors = Boolean.parseBoolean(split[3]);
        if (room == null) plugin.getLogger().warning("For some reason room with id " + split[1] + " doesn't exist at the time of encounter activation!");

        cooldown = Integer.parseInt(split[4])*20;
        loadHordes(split[5]);
    }

    /**
     *
     * @param str formatted as horde1%horde2%...
     */
    private void loadHordes(String str){
        String[] split = str.split("%");
        hordes = new ArrayList<>();
        for (String entry : split) {
            Horde horde = new HordeBuilder(entry).build();
            horde.setLevel(room.getLevel());
            hordes.add(horde);
        }
    }

    public boolean trigger(){
        if (!canTrigger) return false;

        runCooldown();
        Horde horde = selectHorde();
        horde.triggerHorde();
        if (openDoors) openDoors(horde);
        System.out.println(222222222);
        canTrigger = false;
        return true;
    }


    private void runCooldown(){
        if (cooldown < 1) return;
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run(){
                if (!room.getLevel().isStarted()){
                    this.cancel();
                    return;
                }
                if (count >= cooldown){
                    count = 0;
                    canTrigger = true;
                    if (!room.getInRoom().isEmpty()) return;
                    Horde horde = selectHorde();
                    horde.triggerHorde();
                    if (openDoors) openDoors(horde);
                }else count++;
            }
        }.runTaskTimer(plugin,1,1);
    }


    private Horde selectHorde(){
        int roll = ThreadLocalRandom.current().nextInt(hordes.size());
        return hordes.get(roll);
    }

    private void openDoors(Horde horde){
        List<Room> rooms = roomsIn(horde);
        for (Room room : rooms){
            room.getDoors().forEach(door->{
                door.setOpen(true);
                door.open();
                System.out.println(9999999);
            });
        }
    }

    private List<Room> roomsIn(Horde horde){
        Set<Room> auxillaryRooms = new HashSet<>();
        Level level = room.getLevel();
        List<Location> locs = horde.getNodes().stream().map(SpawnNode::getLoc).toList();
        List<Room> rooms = level.getRooms();
        for (Location loc : locs){
            for (Room room : rooms){
                if (room.isInRoom(loc)) auxillaryRooms.add(room);
            }
        }
        auxillaryRooms.add(room);
        return new ArrayList<>(auxillaryRooms);
    }







}
