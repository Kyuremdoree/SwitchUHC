package switchuhc.switchuhc.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import switchuhc.switchuhc.SwitchUHC;
import switchuhc.switchuhc.enums.GameStade;

public class InvDepart implements CommandExecutor {
    private SwitchUHC main;

    public InvDepart(SwitchUHC pl)
    {
        this.main = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(main.getEtat() != GameStade.Waiting) return false;
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (!(player.isOp())) return false;
        if(command.getName().equals("invDepart"))
        {
            if (args.length > 0) return false;
            player.openInventory(main.getInvDepart());
            return true;
        }
        return false;
    }
}
