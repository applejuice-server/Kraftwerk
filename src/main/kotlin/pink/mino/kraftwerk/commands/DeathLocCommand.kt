package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.floor

class DeathLocCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return false
        }
        if (!SpecFeature.instance.isSpec(sender)) {
            Chat.sendMessage(sender, "&cYou must be a spectator to use this command!")
            return false
        }
        if (args.size != 1) {
            Chat.sendMessage(sender, "${Chat.dash} Usage: &c/deathloc <player>")
            return false
        }
        val target = sender.server.getOfflinePlayer(args[0])
        if (!RespawnFeature.instance.respawnablePlayers.contains(target.uniqueId)) {
            Chat.sendMessage(sender, "&c${target.name} hasn't died yet.")
            return false
        }
        sender.teleport(RespawnFeature.instance.locations[target.uniqueId]!!)
        Chat.sendMessage(sender, "${Chat.dash} Teleported to &f${target.name}&7's death location.")
        Chat.sendMessage(sender, "${Chat.dash} Coordinates: &cX: ${floor(RespawnFeature.instance.locations[target.uniqueId]!!.x)}, Y: ${floor(RespawnFeature.instance.locations[target.uniqueId]!!.y)}, &cZ: ${floor(RespawnFeature.instance.locations[target.uniqueId]!!.z)}")

        return true
    }

}