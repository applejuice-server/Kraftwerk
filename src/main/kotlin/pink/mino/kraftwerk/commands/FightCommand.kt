package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import java.util.*
import kotlin.math.floor

class FightCommand : CommandExecutor {
    var cooldowns = HashMap<UUID, Long>()

    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as a console sender.")
            return false
        }
        if (Kraftwerk.instance.game == null) {
            Chat.sendMessage(sender, "&cThere is no game running at the moment.")
            return false
        }
        if (Kraftwerk.instance.game!!.pvpHappened == false) {
            Chat.sendMessage(sender, "&cPvP hasn't occurred yet.")
            return false
        }
        val cooldownTime = 300
        if (cooldowns.containsKey(sender.uniqueId)) {
            val secondsLeft: Long = cooldowns[sender.uniqueId]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
            if (secondsLeft > 0) {
                sender.sendMessage(Chat.colored("&cYou can't use this command for another $secondsLeft second(s)!"))
                return false
            }
        }
        cooldowns[sender.uniqueId] = System.currentTimeMillis()
        Chat.broadcast("&8[${Chat.primaryColor}&lPvP&8] &f${sender.name}&7 is looking for a fight at ${Chat.secondaryColor}X: ${floor(sender.location.x)}&7, ${Chat.secondaryColor}Y: ${floor(sender.location.y)}&7, ${Chat.secondaryColor}Z: ${floor(sender.location.z)}&7!")
        return true
    }
}