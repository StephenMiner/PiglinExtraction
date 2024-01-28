package piglinextraction.me.stephenminer.levels.rooms;

import org.bukkit.Location;
import piglinextraction.me.stephenminer.PiglinExtraction;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;

public class JammedDoor extends Door{
    private boolean jammed;
    private final int jamTime;
    private Horde horde;
    public JammedDoor(PiglinExtraction plugin, String name, Room room, Location loc1, Location loc2, int jamTime, Horde horde) {
        super(plugin, name, room, loc1, loc2);
        this.jamTime = jamTime;
        jammed = true;
    }



    @Override
    public void save(){
        super.save();
        String path = "rooms." + room.getId() + ".doors." + name;
        plugin.roomsFile.getConfig().set(path + ".jam-time",jamTime);
    }


    public int getJamTime(){ return jamTime; }

    public boolean isJammed(){ return jammed; }
    public void setJammed(boolean jammed){ this.jammed = jammed; }

    public Horde getHorde(){ return horde; }
}
