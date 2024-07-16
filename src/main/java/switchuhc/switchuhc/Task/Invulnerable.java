package switchuhc.switchuhc.Task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import switchuhc.switchuhc.SwitchUHC;

public class Invulnerable extends BukkitRunnable {
    private SwitchUHC main;

    private Player p;

    private int timer = 5;

    public Invulnerable(SwitchUHC pl, Player player)
    {
        this.main = pl;
        this.p = player;
    }

    @Override
    public void run()
    {
        p.setNoDamageTicks(200);
        if(timer == 0){
            p.setNoDamageTicks(0);
            cancel();
        }
        else timer--;
    }
}
