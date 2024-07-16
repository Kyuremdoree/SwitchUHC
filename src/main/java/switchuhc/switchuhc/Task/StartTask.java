package switchuhc.switchuhc.Task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.enums.GameStade;

import java.util.List;
import java.util.Random;

public class StartTask extends BukkitRunnable {
    private SwitchUHC main;

    private int timer = 5;
    public StartTask(SwitchUHC pl) { this.main = pl;}

    @Override
    public void run()
    {
        if (timer == 0) {
            Bukkit.broadcastMessage("Début des téléportations !");
            Random random = new Random();
            Location Spawn;
            for (Player p : main.getPlayerList()) {
                p.getInventory().clear();
                for(int i=0; i < (4*9); i++)
                {
                    if(main.getInvDepart().getItem(i) != null)
                    {
                        p.getInventory().setItem(i, main.getInvDepart().getItem(i).clone());
                    }
                    else continue;
                }
                p.setHealth(20);
                p.setFoodLevel(20);
            }
            Player leader;
            for (List<Player> lp : main.getListDesEquipe())
            {
                Spawn = new Location(Bukkit.getWorld("world"), random.nextInt(2 * main.getBorder() + 1) - main.getBorder(), 100, random.nextInt(2 * main.getBorder() + 1) - main.getBorder());
                for(Player p : lp ) {p.teleport(Spawn);}
            }
            main.setEtat(GameStade.Starting);
            TpTask task = new TpTask(this.main);
            task.runTaskTimer(this.main,0,20);
            cancel();
        }
        else {
            Bukkit.broadcastMessage("Démarrage dans §b" + String.valueOf(timer) + " §f!");
            timer--;
        }
    }
}
