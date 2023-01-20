package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.InvSeeFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder

class InvseeCommand : CommandExecutor {

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

        if (!SpecFeature.instance.isSpec(sender)) {
            Chat.sendMessage(sender, "&cYou must be a spectator to use this command!")
            return false
        }

        if (args!!.size != 1) {
            Chat.sendMessage(sender, "${Chat.dash} Usage: &c/invsee <player>")
            return false
        }

        val target = sender.server.getPlayer(args[0])

        if (target == null) {
            Chat.sendMessage(sender, "&cPlayer not online!")
            return false
        }

        if (SpecFeature.instance.isSpec(target)) {
            Chat.sendMessage(sender, "&cYou cannot see the inventory of a spectator!")
            return false
        }

        val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "${target.name}'s Inventory"))
        sender.openInventory(gui.make())
        InvSeeFeature(sender, target).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 20L)
        Chat.sendMessage(sender, "${Chat.prefix} You have opened the inventory of &c${target.name}&7.")
        return true
    }


}