package switchuhc.switchuhc.Task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.enums.GameStade;

public class TpTask extends BukkitRunnable {
    private SwitchUHC main;
    public TpTask(SwitchUHC pl)
    {
        this.main = pl;
    }

    private int timer = 5;

    @Override
    public void run()
    {
        if(timer == 0)
        {
            cancel();
            Bukkit.broadcastMessage("Téléportation Terminé !");
            main.setEtat(GameStade.Mining);
            Bukkit.broadcastMessage("La partie COMMENCE !");
            GameTask task = new GameTask(this.main);
            task.runTaskTimer(this.main,0,20);
        }
        timer--;
    }
}
