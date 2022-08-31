package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PerkChecker

class LapisCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return false
        }
        if (!PerkChecker.checkPerk(sender, "uhc.donator.togglePickups")) {
            Chat.sendMessage(sender, "&cYou must be &6Gold&c to use this command. Buy it at &eapplejuice.tebex.io")
            return false
        }
        if (PickupFeature.instance.lapisPlayers.contains(sender)) {
            PickupFeature.instance.lapisPlayers.remove(sender)
            Chat.sendMessage(sender, "${Chat.dash} &7You have enabled &1Lapis&7 pickups!")
        } else {
            PickupFeature.instance.lapisPlayers.add(sender)
            Chat.sendMessage(sender, "${Chat.dash} &7You have disabled &1Lapis&7 pickups!")
        }
        return true
    }
}