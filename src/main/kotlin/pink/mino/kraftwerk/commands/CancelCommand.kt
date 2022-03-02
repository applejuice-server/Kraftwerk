package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class CancelCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.matchpost")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        SettingsFeature.instance.data!!.set("matchpost.cancelled", true)
        SettingsFeature.instance.data!!.set("whitelist.enabled", true)
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
        }, 900L)
        Chat.sendMessage(sender, "${Chat.prefix} Cancelled the game, any automated tasks will be shortly stopped. The server will restart in 45 seconds.")
        return true
    }

}