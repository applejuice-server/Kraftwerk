package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class FullbrightCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (sender !is Player) {
            sender!!.sendMessage("You must be a player to use this command!")
            return false
        }
        val player = sender
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION)
            Chat.sendMessage(player, "${Chat.prefix} You have &cdisabled &7your fullbright.")
            JavaPlugin.getPlugin(Kraftwerk::class.java).fullbright.remove(player.name.lowercase())
        } else {
            player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 1028391820, 0, false, false))
            Chat.sendMessage(player, "${Chat.prefix} You have &aenabled &7your fullbright.")
            JavaPlugin.getPlugin(Kraftwerk::class.java).fullbright.add(player.name.lowercase())
        }
        return true
    }
}