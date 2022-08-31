package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class BorderCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.border")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.dash} Invalid usage: &f/border <radius>&7.")
            return false
        }
        if (args[0].toIntOrNull() == null) {
            Chat.sendMessage(sender, "${Chat.dash} Invalid border size: &f/border <radius>&7.")
            return false
        }
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "wb ${SettingsFeature.instance.data!!.getString("pregen.world")} setcorners ${args[0]} ${args[0]} -${args[0]} -${args[0]}"
        )
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            val border = Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).worldBorder
            border.size = args[0].toDouble() * 2
            border.setCenter(0.0, 0.0)
        }, 5L)
        SettingsFeature.instance.data!!.set("pregen.border", args[0].toInt())
        SettingsFeature.instance.saveData()
        if (sender !is ConsoleCommandSender) {
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The world border has been set to &f${args[0]}x${args[0]}&7."))
        }
        return true
    }
}