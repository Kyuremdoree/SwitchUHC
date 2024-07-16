package switchuhc.switchuhc.Listeners;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.Task.StartTask;
import switchuhc.switchuhc.enums.GameStade;

import javax.swing.*;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class TeamListerner implements Listener {
    private SwitchUHC main;

    private String nom = "§4Team Selector";

    public  TeamListerner(SwitchUHC pl)
    {
        this.main = pl;
    }

    @EventHandler
    public void OnJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if (player.getCustomName() != null)
        {
            player.setDisplayName(player.getCustomName().substring(0, 2) + player.getName() + "§f");
        }
        main.createScoreboard(player);


        if (main.getEtat() == GameStade.Waiting) {
            main.getPlayerList().add(player);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setDisplayName(null);
            player.setCustomName(null);
            player.setHealth(20);
            player.setFoodLevel(25);
            player.teleport(new Location(Bukkit.getWorld("world"), 0,150,0));
            player.setGameMode(GameMode.ADVENTURE);
            player.getInventory().setItem(4, main.getItTeamSelect());
            if (player.isOp()) {
                player.getInventory().setItem(8,main.getItStart());
            }
        }

        if (main.getEtat() != GameStade.Waiting) {
            /*
            if(player.getCustomName() != null) player.setDisplayName(player.getCustomName().substring(0, 2) + player.getName() + "§f");
            for(int i = 0; 1+main.getListTeam().size() > i+1; i++)
            {
                if(main.getListTeam().get(i).getName().equals(player.getCustomName()))
                {
                    main.getListTeam().get(i).addEntry(player.getName());
                    break;
                }
            }
            for (int i = 0; i < main.getListTeam().size(); i++) {
                if (main.getListTeam().get(i).getEntries().contains(player.getName())) {
                    String teamPrefix = main.getListTeam().get(i).getPrefix();
                    String playerName = player.getName();
                    player.setDisplayName(teamPrefix + playerName + "§f");
                    player.setPlayerListName(player.getDisplayName());
                    break;
                }
            }
            */
        }
        for(Player p : Bukkit.getOnlinePlayers())
        {
            main.updateScoreBoard(p);
        }
    }

    @EventHandler
    public void onInterractTeamSelection(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack it = event.getItem();
        if (it == null) return;
        if (it.getType() == Material.BANNER && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equals("§fTeam"))
        {
            player.openInventory(main.getMenuTeamSelector());
        }
        if (it.getType() == Material.SLIME_BALL && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equalsIgnoreCase("§aStart"))
        {
            if (main.getEtat() == GameStade.Waiting && main.getPlayerList().size() % main.getNbMembre() == 0)
            {
                for (Player pl: main.getPlayerList())
                {
                    if (pl.getCustomName() == null) {
                        Bukkit.broadcastMessage("§4Toutes les équipe ne sont pas composée !");
                        return;
                    }
                }
                main.ChargementEquipe();
                for(Player p : main.getPlayerList())
                {
                    p.getInventory().clear();
                    p.setGameMode(GameMode.SURVIVAL);
                }
                StartTask task = new StartTask(this.main);
                task.runTaskTimer(main,0,20);
            }
        }
    }
    @EventHandler
    public void onInventoryTeamSelection(InventoryClickEvent event)
    {
        if(!event.getView().getTitle().equals(nom))
        {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        String JoinTeam = "§b>> §f" + player.getName();
        ItemMeta meta;
        List<String> ListLore;

        event.setCancelled(true);

        if (current == null) return;
        if (current.getType() == Material.SKULL_ITEM && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && player.isOp())
        {
            switch (current.getItemMeta().getDisplayName())
            {
                case "§2Plus":
                    main.setNbMembre(main.getNbMembre() + 1);
                    break;
                case "§4Moins":
                    main.setNbMembre(main.getNbMembre() - 1);
                    break;
            }
            for (ItemStack it : event.getView().getTopInventory().getContents())
            {
                if (it != null && it.hasItemMeta() && it.getItemMeta().hasDisplayName() && it.getItemMeta().getDisplayName().equalsIgnoreCase(" "))
                {
                    it.setAmount(main.getNbMembre());
                }
            }
        }
        if (current.getType() == Material.BANNER && current.hasItemMeta()) {
            if (current.getItemMeta().getLore().size() == main.getNbMembre() + 1)
            {
                player.sendMessage("équipe complète !");
                player.closeInventory();
                return;
            }
            for (ItemStack it : event.getView().getTopInventory().getContents()) {
                if (it != null && it.hasItemMeta() && it.getItemMeta().hasLore() && it.getItemMeta().getLore().contains(JoinTeam)) {
                    meta = it.getItemMeta();
                    ListLore = meta.getLore();
                    ListLore.remove(JoinTeam);
                    meta.setLore(ListLore);
                    it.setItemMeta(meta);
                }
            }
            meta = current.getItemMeta();
            ListLore = meta.getLore();
            ListLore.add(JoinTeam);
            meta.setLore(ListLore);
            current.setItemMeta(meta);
            ItemStack Banner = main.getItTeamSelect();
            player.getInventory().remove(Banner);
            byte couleur = (byte) current.getDurability();
            Banner.setDurability(couleur);
            player.getInventory().setItem(4, Banner);
            /*
            for(int i =0; i < main.getListTeam().size();i++)
            {
                if(main.getListTeam().get(i).getName().equals(player.getCustomName()))
                {
                    main.getListTeam().get(i).removeEntry(player.getName());
                    break;
                }
            }
            */
            player.setDisplayName(current.getItemMeta().getDisplayName().substring(0, 2) + player.getName() + "§f")
            player.setCustomName(current.getItemMeta().getDisplayName());
            /*
            for(int i =0; i < main.getListTeam().size();i++)
            {
                if(main.getListTeam().get(i).getName().equals(player.getCustomName()))
                {
                    main.getListTeam().get(i).addEntry(player.getName());
                    break;
                }
            }
            */
            main.updateScoreBoard(player);
            main.updateEquipe(player);
            player.closeInventory();
        }
        return;
    }


    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (main.getEtat() == GameStade.Waiting) {
            player.getInventory().clear();
            player.setDisplayName(player.getName());
            main.scoreboard.getTeam(player.getCustomName()).removeEntry(player.getName());
            for (Player p: Bukkit.getOnlinePlayers()) main.updateAllEquipe(p);
            player.setCustomName(null);
            main.getPlayerList().remove(player);
            String JoinTeam = "§b>> §f" + player.getName();
            List<String> ListLore;
            for (ItemStack it : main.getMenuTeamSelector()) {
                if (it != null && it.hasItemMeta() && it.getItemMeta().hasLore() && it.getItemMeta().getLore().contains(JoinTeam)) {
                    ItemMeta meta = it.getItemMeta();
                    ListLore = meta.getLore();
                    ListLore.remove(JoinTeam);
                    meta.setLore(ListLore);
                    it.setItemMeta(meta);
                }
            }
            for(List<Player> lp : main.getListDesEquipe())
            {
                if(lp.contains(player))  lp.remove(player);
            }
            for(Player p : Bukkit.getOnlinePlayers())
            {
                main.updateScoreBoard(p);
            }
            return;
        }
        /*
        for(int i =0; i < main.getListTeam().size();i++)
        {
            if(main.getListTeam().get(i).getName().equals(player.getCustomName()))
            {
                main.getListTeam().get(i).removeEntry(player.getName());
                break;
            }
        }

         */
        for(Player p : Bukkit.getOnlinePlayers())
        {
            main.updateScoreBoard(p);
        }
    }
}
