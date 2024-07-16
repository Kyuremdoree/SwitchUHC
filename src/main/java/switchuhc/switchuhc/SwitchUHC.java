package switchuhc.switchuhc;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import switchuhc.switchuhc.Commands.FriendlyFire;
import switchuhc.switchuhc.Commands.InvDepart;
import switchuhc.switchuhc.Listeners.GameListener;
import switchuhc.switchuhc.Listeners.TeamListerner;
import switchuhc.switchuhc.classes.Switch;
import switchuhc.switchuhc.enums.GameStade;


import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

import java.util.UUID;

public final class SwitchUHC extends JavaPlugin {

    private boolean activ = true;

    private int TempsBordure = 10*60;
    public int getTempsBordure() {return TempsBordure;}
    public void setTempsBordure(int i) { if(i >= 3600 && i <= 7200) TempsBordure = i;}
    public void DecrementeTempsBordure() { TempsBordure--;}

    public boolean ModeSwitch = true;

    public int nbMinSwitch = 60 / 2;

    private int TempsPvp = 1*60;
    public int getTempsPvp() { return TempsPvp;}
    public void setTempsPvp(int i) {if(i >= 600 && i <= 2400) TempsBordure = i;}
    public void DecrementeTempsPVP() {TempsPvp--;}

    public int TempsJeu = 0;
    private int Border = 1000;

    public int getBorder(){ return Border;}
    public void setBorder(int i)
    {
        if (i > 100 && i <= 2000) Border = i;
    }

    private List<Player> PlayerList = new ArrayList<Player>();

    public List<Player> getPlayerList()
    {
        return PlayerList;
    }

    private ItemStack ItTeamSelect = new ItemStack(Material.BANNER, 1, (byte)15);

    public ItemStack getItTeamSelect()
    {
        return ItTeamSelect.clone();
    }

    private Inventory MenuTeamSelector = Bukkit.createInventory(null, 9*4, "§4Team Selector");

    public Inventory getMenuTeamSelector()
    {
        return MenuTeamSelector;
    }

    private Inventory InvDepart = Bukkit.createInventory(null, 9*4, "§6Inventaire de Départ");

    public Inventory getInvDepart(){ return InvDepart;}

    private ItemStack ItStart = new ItemStack(Material.SLIME_BALL, 1);

    public ItemStack getItStart() { return ItStart.clone(); }

    public Switch Changement = new Switch(this);

    public void ItemCompteur()
    {
        ItemStack plus = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        ItemStack compteur = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        ItemStack moins = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
        SkullMeta metaPlus = (SkullMeta) plus.getItemMeta();
        SkullMeta metaCompteur = (SkullMeta) compteur.getItemMeta();
        SkullMeta metaMoins = (SkullMeta) moins.getItemMeta();
        metaPlus.setDisplayName("§2Plus");
        metaMoins.setDisplayName("§4Moins");
        metaCompteur.setDisplayName(" ");
        plus.setItemMeta(metaPlus);
        compteur.setItemMeta(metaCompteur);
        moins.setItemMeta(metaMoins);
        compteur.setAmount(getNbMembre());
        MenuTeamSelector.setItem(MenuTeamSelector.getSize() - 1, plus);
        MenuTeamSelector.setItem(MenuTeamSelector.getSize() - 2, compteur);
        MenuTeamSelector.setItem(MenuTeamSelector.getSize() - 3, moins);
    }

    private int nbMembre = 2;
    public int getNbMembre() { return nbMembre; }
    public void setNbMembre(int i) { if (i > 1 && i <= 10) { nbMembre = i;} }

    private GameStade Etat = GameStade.Waiting;
    public GameStade getEtat() { return Etat; }
    public void setEtat(GameStade newEtat) { Etat = newEtat; }
    private List<List<Player>> ListDesEquipe = new ArrayList<>();
    public List<List<Player>> getListDesEquipe() { return ListDesEquipe;}

    private List<Team> listTeam = new ArrayList<>();
    public List<Team> getListTeam() { return listTeam;}

    public Map<Player, Scoreboard> MapPlayerScore = new HashMap<>();

    public Scoreboard scoreboard;

    public WorldBorder wb;

    public boolean friendlyFire = false;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[SwitchUHC] Enable");
        Bukkit.getWorlds().forEach(world -> world.setGameRuleValue("naturalRegeneration", "false"));
        World world = Bukkit.getWorld("world");
        wb = world.getWorldBorder();
        wb.setCenter(0,0);
        wb.setSize(getBorder()*2);
        wb.setDamageAmount(2);
        getCommand("friendlyfire").setExecutor((new FriendlyFire(this)));
        getCommand("invDepart").setExecutor(new InvDepart(this));
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        if (!activ) return;
        Team team;
        for (int i=0; i<15; i++)
        {
            MenuTeamSelector.addItem(CreateBanner(i));
            team = scoreboard.registerNewTeam(ColorBanner(i));
            team.setPrefix(ColorBanner(i).substring(0, 2));
        }
        ItemCompteur();
        ItemMeta meta = ItTeamSelect.getItemMeta();
        meta.setDisplayName("§fTeam");
        ItTeamSelect.setItemMeta(meta);
        meta = ItStart.getItemMeta();
        meta.setDisplayName("§aStart");
        ItStart.setItemMeta(meta);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TeamListerner(this), this);
        pm.registerEvents(new GameListener(this), this);
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                world.setTime(0);
            }
        }, 0, 20 * 60 * 5);
    }

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            player.getInventory().clear();
            player.setDisplayName(player.getName());
            player.setCustomName(null);
        }
    }

    private ItemStack CreateBanner(int i)
    {
        ItemStack banner = new ItemStack(Material.BANNER, 1, (byte)i);
        ItemMeta meta = banner.getItemMeta();
        meta.setDisplayName(ColorBanner(i));
        List<String> ListLore = new ArrayList<>();
        ListLore.add("§b§oCliquez-ici !");
        meta.setLore(ListLore);
        banner.setItemMeta(meta);
        return banner;
    }

    private String ColorBanner(int i)
    {
        String color;
        switch (i)
        {
            case 0:
                color = "§0Noir";
                break;
            case 1:
                color = "§4Rouge";
                break;
            case 2:
                color = "§2Vert";
                break;
            case 3:
                color = "§6Marron";
                break;
            case 4:
                color = "§1Bleu";
                break;
            case 5:
                color = "§5Violet";
                break;
            case 6:
                color = "§bCyan";
                break;
            case 7:
                color = "§7Gris clair";
                break;
            case 8:
                color = "§8Gris";
                break;
            case 9:
                color = "§dRose";
                break;
            case 10:
                color = "§aVert lime";
                break;
            case 11:
                color = "§eJaune";
                break;
            case 12:
                color = "§9Bleu clair";
                break;
            case 13:
                color = "§cMagenta";
                break;
            case 14:
                color = "§6Orange";
                break;
            case 15: default:
                color = "Blanc";
                break;
        }
        return color;
    }


    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("example", "dummy");
        objective.setDisplayName("§kcac§b Monténégro Land §f§kcac");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore("§b====================").setScore(12);
        objective.getScore("§6Nom : §f" + player.getName()).setScore(10);

        Team Equipe = objective.getScoreboard().registerNewTeam("Equipe");
        Equipe.addEntry("§6Team : §f");
        if (player.getCustomName() == null) Equipe.setSuffix("Aucune");
        else Equipe.setSuffix(player.getCustomName());
        objective.getScore("§6Team : §f").setScore(9);

        Team NbJoueur = objective.getScoreboard().registerNewTeam("NbJoueur");
        NbJoueur.addEntry("§6Nombre Joueurs : §f");
        NbJoueur.setSuffix(String.valueOf(getPlayerList().size()) + "(" + String.valueOf(getListDesEquipe().size()) + ")");
        objective.getScore("§6Nombre Joueurs : §f").setScore(7);

        objective.getScore(" ").setScore(6);

        Team tmpJeu = objective.getScoreboard().registerNewTeam("tmpJeu");
        tmpJeu.addEntry("§6Temps de jeu : §f");
        tmpJeu.setSuffix(TimeConverter(TempsJeu));
        objective.getScore("§6Temps de jeu : §f").setScore(5);

        Team tmpPVP = objective.getScoreboard().registerNewTeam("tmpPVP");
        tmpPVP.addEntry("§6PVP : §f");
        if (getTempsPvp() < 0) tmpPVP.setSuffix("✔");
        else tmpPVP.setSuffix(TimeConverter(getTempsPvp()));
        objective.getScore("§6PVP : §f").setScore(4);

        Team tmpBord = objective.getScoreboard().registerNewTeam("tmpBord");
        tmpBord.addEntry("§6Bordure : §f");
        if (getTempsBordure() < 0) tmpBord.setSuffix("✔");
        else tmpBord.setSuffix(TimeConverter(getTempsBordure()));
        objective.getScore("§6Bordure : §f").setScore(3);

        objective.getScore("").setScore(2);
        objective.getScore("§bplay.montenegroland.fr").setScore(1);
        player.setScoreboard(board);

        Team team;
        for (int i=0; i<15; i++)
        {
            team = board.registerNewTeam(ColorBanner(i));
            team.setPrefix(ColorBanner(i).substring(0, 2));
        }
        updateAllEquipe(player);

    }

    public void updateScoreBoard(Player player)
    {
        if (player.getCustomName() == null) player.getScoreboard().getTeam("Equipe").setSuffix("Aucune");
        else player.getScoreboard().getTeam("Equipe").setSuffix(player.getCustomName());

        player.getScoreboard().getTeam("NbJoueur").setSuffix(String.valueOf(getPlayerList().size()) + "(" + String.valueOf(getListDesEquipe().size()) + ")");

        player.getScoreboard().getTeam("tmpJeu").setSuffix(TimeConverter(TempsJeu));

        if (getTempsPvp() < 0) player.getScoreboard().getTeam("tmpPVP").setSuffix("✔");
        else player.getScoreboard().getTeam("tmpPVP").setSuffix(TimeConverter(getTempsPvp()));

        if (getTempsBordure() < 0) player.getScoreboard().getTeam("tmpBord").setSuffix("✔");
        else player.getScoreboard().getTeam("tmpBord").setSuffix(TimeConverter(getTempsBordure()));
    }

    public void updateEquipe(Player player)
    {
        if(player.getCustomName() == null) return;
        boolean find = false;
        for(Team t : scoreboard.getTeams())
        {
            for(String s : t.getEntries())
            {
                if(s.equals(player.getName()))
                {
                    t.removeEntry(player.getName());
                    find = true;
                    break;
                }
            }
            if(find)break;
        }
        scoreboard.getTeam(player.getCustomName()).addEntry(player.getName());
        for(Player p : Bukkit.getOnlinePlayers()) updateAllEquipe(p);
    }

    public void updateAllEquipe(Player player)
    {
        for (Team t : scoreboard.getTeams()) {
            if (t != null) {
                String teamName = t.getName();
                if (player.getScoreboard().getTeam(teamName) != null) {
                    Team playerTeam = player.getScoreboard().getTeam(teamName);
                    playerTeam.unregister(); // Supprimer l'équipe existante dans le scoreboard du joueur
                }

                Team newPlayerTeam = player.getScoreboard().registerNewTeam(teamName); // Recréer l'équipe
                newPlayerTeam.setPrefix(t.getPrefix());
                newPlayerTeam.setSuffix(t.getSuffix());

                for (String entry : t.getEntries()) {
                    newPlayerTeam.addEntry(entry);
                }
            }
        }
        player.setScoreboard(player.getScoreboard());
    }

    public String TimeConverter(int i)
    {
        int hours = i / 3600;
        int minutes = (i % 3600) / 60;
        int seconds = i % 60;

        // Utilisez la classe DecimalFormat pour formater les nombres à deux chiffres (ajoute un 0 devant si nécessaire)
        DecimalFormat decimalFormat = new DecimalFormat("00");
        if (hours == 0) return decimalFormat.format(minutes) + ":" + decimalFormat.format(seconds);

        return decimalFormat.format(hours) + ":" + decimalFormat.format(minutes) + ":" + decimalFormat.format(seconds);
    }

    public void ChargementEquipe()
    {
        List<List<Player>> Chargement = new ArrayList<>();
        for(Player p : getPlayerList())
        {
            boolean equipeexiste = false;
            for(List<Player> equipe : Chargement)
            {
                if(equipe.get(0).getCustomName().equals(null))
                {
                    Bukkit.broadcastMessage("§4Toutes les équipe ne sont pas composée !");
                    return;
                }
                if(equipe.get(0).getCustomName().equals(p.getCustomName()))
                {
                    equipe.add(p);
                    equipeexiste = true;
                    break;
                }
            }
            if (!equipeexiste)
            {
                List<Player> nouvelleEquipe = new ArrayList<>();
                nouvelleEquipe.add(p);
                Chargement.add(nouvelleEquipe);
            }
        }
        ListDesEquipe = Chargement;
    }



    public boolean CheckWin()
    {
        if(getListDesEquipe().size() == 1)
        {
            return true;
        }
        return false;
    }
}
