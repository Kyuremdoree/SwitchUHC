package switchuhc.switchuhc.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.enums.GameStade;

public class FriendlyFire implements CommandExecutor {
    private SwitchUHC main;

    public FriendlyFire(SwitchUHC pl) { this.main = pl;}

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(main.getEtat() != GameStade.Waiting) return false;
        if(!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if(command.getName().equals("friendlyfire"))
        {
            if(args.length == 0 || args.length > 1)
            {
                if (main.friendlyFire) player.sendMessage("FriendlyFire Activée !");
                else player.sendMessage("FriendlyFire Désactivée !");
                player.sendMessage("Changer l'état : /friendlyfire <on/off>");
                return false;
            }
            if(args.length == 1)
            {
                if(args[0].toString().contains("on"))
                {
                    main.friendlyFire = true;
                    player.sendMessage("FriendlyFire Activée !");
                    return true;
                }
                if(args[0].toString().contains("off"))
                {
                    main.friendlyFire = false;
                    player.sendMessage("FriendlyFire Désactivée !");
                    return true;
                }
            }
        }
        return false;
    }
}
