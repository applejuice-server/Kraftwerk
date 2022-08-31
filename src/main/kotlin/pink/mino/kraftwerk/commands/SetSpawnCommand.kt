package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class SetSpawnCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return true
        }
        if (!sender.hasPermission("uhc.staff.spawn")) {
            Chat.sendMessage(sender, "&cYou do not have permission to use this command.")
            return true
        }
        SettingsFeature.instance.data!!.set("config.spawn.x", sender.location.x)
        SettingsFeature.instance.data!!.set("config.spawn.y", sender.location.y)
        SettingsFeature.instance.data!!.set("config.spawn.z", sender.location.z)
        SettingsFeature.instance.data!!.set("config.spawn.yaw", sender.location.yaw)
        SettingsFeature.instance.data!!.set("config.spawn.pitch", sender.location.pitch)
        SettingsFeature.instance.data!!.set("config.spawn.world", sender.location.world.name)
        SettingsFeature.instance.saveData()
        Chat.sendMessage(sender, "${Chat.dash} The spawn has been set to your location, use &c/spawn&7 to teleport to the new location.")
        return true
    }
}