package piglinextraction.me.stephenminer.mobs.boss.encounters;

import com.google.common.collect.Lists;
import org.checkerframework.checker.units.qual.A;
import piglinextraction.me.stephenminer.levels.builders.HordeBuilder;
import piglinextraction.me.stephenminer.mobs.hordes.Horde;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomHorde extends RandomEncounter{
    private List<Horde> hordes;
    /**
     * @param str formatted as "randomhorde=horde1%horde2%..."
     */
    public RandomHorde(String str) {
        super("randomhorde");
        loadHordes(str);
    }

    /**
     * @param str formatted as "horde1%horde2%..."
     */
    private void loadHordes(String str){
        hordes = new ArrayList<>();
        String[] ids = str.split("%");
        for (String id : ids) {
            Horde horde = new HordeBuilder(id).build();
            hordes.add(horde);
        }
    }

    @Override
    public boolean trigger() {
        int rolls = ThreadLocalRandom.current().nextInt(9) +1;
        int roll = 0;
        Collections.shuffle(hordes);
        for (int i = 0; i < rolls; i++){
            roll = ThreadLocalRandom.current().nextInt(hordes.size());
        }
        Horde horde = hordes.get(roll);
        horde.triggerHorde();
        return true;
    }
}
