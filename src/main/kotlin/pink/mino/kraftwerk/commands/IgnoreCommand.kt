package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class IgnoreCommand : CommandExecutor {
    /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "&cYou must provide a player to ignore.")
            return false
        }
        if (sender !is Player) {
            sender.sendMessage("You don't need this buddy.")
            return false
        }
        val player = Bukkit.getPlayer(args[0])
        if (player == null) {
            Chat.sendMessage(sender, "&cYou must provide a valid (and online) player to ignore.")
            return false
        }
        if (player.hasPermission("uhc.staff")) {
            Chat.sendMessage(sender, "&cYou can't ignore this player, they are a staff member, contact us on Discord regarding feedback.")
            return false
        }
        val list = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.ignored
        if (list.contains(player.uniqueId)) {
            list.remove(player.uniqueId)
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.ignored = list
            Chat.sendMessage(sender, "${Chat.prefix} Successfully removed &8'${Chat.secondaryColor}${player.name}&8'&7 from your ignored list.")
        } else {
            list.add(player.uniqueId)
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.ignored = list
            Chat.sendMessage(sender, "${Chat.prefix} Successfully added &8'${Chat.secondaryColor}${player.name}&8'&7 to your ignored list.")
        }

        return true
    }

}