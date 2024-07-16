package switchuhc.switchuhc.Task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.enums.GameStade;

public class GameTask extends BukkitRunnable {
    private SwitchUHC main;

    private int pvptime;
    private int borduretime;
    public GameTask(SwitchUHC pl)
    {
        this.main = pl;
        pvptime = main.getTempsPvp();
        borduretime = main.getBorder();
    }

    @Override
    public void run()
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            main.updateScoreBoard(p);
        }
        if (main.TempsJeu == 0)
        {
            Bukkit.broadcastMessage("§eVous êtes invunérable au dégat pendant 30 seconde !");
        }
        if (main.TempsJeu == 30)
        {
            Bukkit.broadcastMessage("§4Vous êtes vunérable au dégat !");
        }

        if (main.getTempsPvp() >= 0)
        {
            switch (main.getTempsPvp())
            {
                case 5:
                    Bukkit.broadcastMessage("§4PvP activé dans 5 !");
                    break;
                case 4:
                    Bukkit.broadcastMessage("§4PvP activé dans 4 !");
                    break;
                case 3:
                    Bukkit.broadcastMessage("§4PvP activé dans 3 !");
                    break;
                case 2:
                    Bukkit.broadcastMessage("§4PvP activé dans 2 !");
                    break;
                case 1:
                    Bukkit.broadcastMessage("§4PvP activé dans 1 !");
                    break;
                case 0:
                    Bukkit.broadcastMessage("§4PvP activé !");
                    break;
            }
            main.DecrementeTempsPVP();
        }
        if (main.TempsJeu== pvptime + 1)
        {
            main.setEtat(GameStade.Meetup);
        }

        if (main.getTempsBordure() >= 0)
        {
            switch (main.getTempsBordure())
            {
                case 5:
                    Bukkit.broadcastMessage("§4Réduction de la Bordure dans 5 !");
                    break;
                case 4:
                    Bukkit.broadcastMessage("§4Réduction de la Bordure dans 4 !");
                    break;
                case 3:
                    Bukkit.broadcastMessage("§4Réduction de la Bordure dans 3 !");
                    break;
                case 2:
                    Bukkit.broadcastMessage("§4Réduction de la Bordure dans 2 !");
                    break;
                case 1:
                    Bukkit.broadcastMessage("§4Réduction de la Bordure dans 1 !");
                    break;
                case 0:
                    Bukkit.broadcastMessage("§4Réduction de la Bordure !");
                    break;
            }
            main.DecrementeTempsBordure();
        }

        if (main.TempsJeu == borduretime + 1)
        {
            Bukkit.getScheduler().runTaskTimer(main, new BukkitRunnable() {
                @Override
                public void run() {
                    if(main.wb.getSize() > 100) main.wb.setSize(main.wb.getSize() - 0.5);
                }
            }, 0, 20);
        }

        if(main.ModeSwitch && (main.TempsJeu + 5)%main.nbMinSwitch == 0)
        {
            SwitchTask task = new SwitchTask(this.main);
            task.runTaskTimer(this.main,0,20);
        }

        if(main.getEtat() == GameStade.Ending)
        {
            Bukkit.broadcastMessage("C'est Gagné");
            cancel();
        }


        main.TempsJeu++;
    }
}
