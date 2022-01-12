package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder

class EditConfigCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        val player = sender as Player
        if (!player.hasPermission("uhc.staff")) {
            Chat.sendMessage(player, "&cYou don't have permission to execute this command.")
            return false
        }
        val gui = GuiBuilder().rows(4).name(ChatColor.translateAlternateColorCodes('&', "&4Edit UHC Config"))

        if (args[0] === "options") {
            TODO("Options")
        } else if (args[0] === "events") {
            TODO("Events")
        } else if (args[0] === "matchpost") {
            TODO("Matchpost")
        } else if (args[0] === "rules") {
            TODO("Rules")
        } else if (args[0] === "mining_rules") {
            TODO("Mining Rules")
        } else if (args[0] === "host") {
            TODO("Host")
        } else if (args[0] === "potions") {
            TODO("Potions")
        }
        player.playSound(player.location, Sound.LEVEL_UP, 10.toFloat(), 10.toFloat())
        player.openInventory(gui.make())
        return true
    }
}