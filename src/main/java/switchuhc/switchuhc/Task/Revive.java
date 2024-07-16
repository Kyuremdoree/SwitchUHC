package switchuhc.switchuhc.Task;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import switchuhc.switchuhc.SwitchUHC;

import java.util.Locale;

public class Revive extends BukkitRunnable {
    private SwitchUHC main;

    private int timer = 4;
    private Player player;

    private Location location;

    public Revive(SwitchUHC pl, Player p, Location l) {
        this.main = pl;
        this.player = p;
        this.location = l;
    }

    @Override
    public void run()
    {
        if(timer == 0) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.teleport(location);
            Invulnerable task = new Invulnerable(this.main, player);
            task.runTaskTimer(this.main,0,20);
            cancel();
        }
        else
        {
            player.sendMessage("Revive dans " + String.valueOf(timer) + " seconds");
            timer--;
        }
    }
}
