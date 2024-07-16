package switchuhc.switchuhc.Task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import switchuhc.switchuhc.SwitchUHC;

public class SwitchTask extends BukkitRunnable {
    private SwitchUHC main;
    private int timer = 5;

    public SwitchTask(SwitchUHC pl)
    {
        main = pl;
    }

    public void run()
    {
        if(timer == 0)
        {
            Bukkit.broadcastMessage("Switch !");
            main.Changement.TakeSwitchedPlayer();
            main.Changement.MakeSwitchPlayer();
            cancel();
        }
        else
        {
            Bukkit.broadcastMessage("Switch dans " + String.valueOf(timer) + " s !");
            timer--;
        }
    }
}
