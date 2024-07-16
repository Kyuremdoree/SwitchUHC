package switchuhc.switchuhc.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import switchuhc.switchuhc.SwitchUHC;

import java.util.*;

public class Switch {
    private SwitchUHC main;

    private List<Player> listJoueurSwitch = new ArrayList<>();

    public Switch(SwitchUHC pl)
    {
        main = pl;
    }

    public void TakeSwitchedPlayer()
    {
        Random random = new Random();
        int i;
        for(List<Player> lp : this.main.getListDesEquipe())
        {
            i = random.nextInt(lp.size());
            listJoueurSwitch.add(lp.get(i));
            lp.remove(lp.get(i));
            main.scoreboard.getTeam(lp.get(i).getCustomName()).removeEntry(lp.get(i).getName());
        }
    }

    public void MakeSwitchPlayer()
    {
        int i = 0;
        int rand;
        Random random = new Random();
        List<Player> listEquipePrise = new ArrayList<>();
        Map<Player, String> lienEquipeJoueur = new HashMap<>();
        Map<Player, Location> coEquipeJoueur = new HashMap<>();
        while(true)
        {
            if(i == listJoueurSwitch.size())
            {
                break;
            }
            lienEquipeJoueur.put(listJoueurSwitch.get(i), null);
            coEquipeJoueur.put(listJoueurSwitch.get(i), null);
            rand = random.nextInt(listJoueurSwitch.size());
            if (!(listEquipePrise.contains(listJoueurSwitch.get(i))) && listEquipePrise.size() == listJoueurSwitch.size() - 1)
            {
                lienEquipeJoueur.put(listJoueurSwitch.get(i), listJoueurSwitch.get(i).getCustomName());
                coEquipeJoueur.put(listJoueurSwitch.get(i), listJoueurSwitch.get(i).getLocation());
                break;
            }
            while (rand == i)
            {
                rand = random.nextInt(listJoueurSwitch.size());
            }
            while (listEquipePrise.contains(listJoueurSwitch.get(rand))) {
                rand = random.nextInt(listJoueurSwitch.size());
            }
            lienEquipeJoueur.put(listJoueurSwitch.get(i), listJoueurSwitch.get(rand).getCustomName());
            coEquipeJoueur.put(listJoueurSwitch.get(i), listJoueurSwitch.get(rand).getLocation());
            listEquipePrise.add(listJoueurSwitch.get(rand));
            i++;
        }
        for (Player p : listJoueurSwitch)
        {
            p.setCustomName(lienEquipeJoueur.get(p));
            p.setDisplayName(p.getCustomName().substring(0, 2) + p.getName() + "§f");
            p.teleport(coEquipeJoueur.get(p));
            main.scoreboard.getTeam(p.getCustomName()).addEntry(p.getName());
            p.setPlayerListName(p.getDisplayName());
        }
        for(Player p : Bukkit.getOnlinePlayers())
        {
            main.updateScoreBoard(p);
        }
    }
}
