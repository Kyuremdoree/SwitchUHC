package switchuhc.switchuhc.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.Task.Revive;
import switchuhc.switchuhc.Task.TpTask;
import switchuhc.switchuhc.enums.GameStade;

import java.util.List;

public class GameListener implements Listener {
    private SwitchUHC main;
    private Location loc;


    public GameListener(SwitchUHC pl)
    {
        this.main = pl;
    }

    @EventHandler
    public void PlayerTakeDamageByPlayer(EntityDamageByEntityEvent event)
    {
        if (event.getEntity() == null || !(event.getEntity() instanceof Player) || event.getDamager() == null || !(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!main.friendlyFire) {
            if(player.getCustomName().equalsIgnoreCase(event.getDamager().getCustomName())) event.setCancelled(true);
            return;
        }
        if (main.TempsJeu > 30 && main.getEtat() == GameStade.Mining && player.getHealth() > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void PlayerTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof Player)) return;
        if (main.TempsJeu <= 30) {
            event.setCancelled(true);
            return;
        }
        Player player = (Player) event.getEntity();
        if (player.getHealth() <= event.getDamage()) {
            if (main.getEtat() == GameStade.Mining) {
                event.setCancelled(true);
                Location co = player.getLocation();
                if (player.getLastDamageCause() != null && (player.getLastDamageCause().equals(EntityDamageEvent.DamageCause.FALL) || player.getLastDamageCause().equals(EntityDamageEvent.DamageCause.FALLING_BLOCK))) {
                    Bukkit.broadcastMessage(player.getDisplayName() + " est mort de chute");
                }
                else {
                    Bukkit.broadcastMessage(player.getDisplayName() + " est mort !");
                }
                player.setGameMode(GameMode.SPECTATOR);
                player.teleport(new Location(Bukkit.getWorld("world"),0,100,0));
                Revive task = new Revive(this.main,player, co);
                task.runTaskTimer(this.main, 0, 20);
                return;
            }
        }
    }
    @EventHandler
    public void PlayerDie(PlayerDeathEvent event)
    {
        event.setDeathMessage(null);
        if(!(event.getEntity() instanceof Player)) return;
        Player player = event.getEntity();

        if (main.getEtat() == GameStade.Meetup)
        {
            Location co = player.getLocation();
            player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
            player.getWorld().dropItem(co, new ItemStack(Material.GOLDEN_APPLE, 1));
            if (player.getKiller() != null) {
                Player player2 = player.getKiller();

                Bukkit.broadcastMessage(player.getDisplayName() + "§f a été tué par " + player2.getDisplayName());
            }
            else Bukkit.broadcastMessage(player.getDisplayName() + " est mort !");
            main.getPlayerList().remove(player);
            for (List<Player> list : main.getListDesEquipe()) {
                if (list.contains(player)) {
                    list.remove(player);
                    if (list.size() == 0) {
                        main.getListDesEquipe().remove(list);
                    }
                    break;
                }
            }
            player.setCustomName(null);
            player.setGameMode(GameMode.SPECTATOR);
            loc = co;
            if (main.CheckWin()) main.setEtat(GameStade.Ending);
        }
    }

    public void PlayerRespawn(PlayerRespawnEvent event)
    {
        if (main.getEtat() == GameStade.Meetup)
        {
            event.getPlayer().teleport(loc);
        }
    }
}
