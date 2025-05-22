package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
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
        ConfigFeature.instance.data!!.set("config.spawn.x", sender.location.x)
        ConfigFeature.instance.data!!.set("config.spawn.y", sender.location.y)
        ConfigFeature.instance.data!!.set("config.spawn.z", sender.location.z)
        ConfigFeature.instance.data!!.set("config.spawn.yaw", sender.location.yaw)
        ConfigFeature.instance.data!!.set("config.spawn.pitch", sender.location.pitch)
        ConfigFeature.instance.data!!.set("config.spawn.world", sender.location.world.name)
        ConfigFeature.instance.saveData()
        Chat.sendMessage(sender, "${Chat.prefix} The spawn has been set to your location, use ${Chat.primaryColor}/spawn&7 to teleport to the new location.")
        return true
    }
}