package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class WinnerCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.winner")) {
                Chat.sendMessage(sender, "&cYou don't have permission to use this command.")
                return false
            }
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "&cYou can't do this right now.")
            return false
        }
        var winners = ConfigFeature.instance.data!!.getStringList("game.winners")
        if (winners == null) {
            ConfigFeature.instance.data!!.set("game.winners", ArrayList<String>())
            winners = ConfigFeature.instance.data!!.getStringList("game.winners")
        }
        if (args.isEmpty()) {
            if (winners.isEmpty()) {
                Chat.sendMessage(sender, "${Chat.prefix} There's no winners at the moment.")
            } else {
                Chat.sendMessage(sender, "${Chat.prefix} Winners: ${Chat.secondaryColor}${winners.joinToString(", ")}")
            }
            return false
        }
        for (argument in args) {
            val player = Bukkit.getOfflinePlayer(argument)
            if (player == null) {
                Chat.sendMessage(sender, "&cInvalid player '${argument}', please provide a valid player.")
                return false
            }
            if (winners.contains(player.name)) {
                winners.remove(player.name)
                ConfigFeature.instance.data!!.set("game.winners", winners)
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} ${Chat.secondaryColor}${player.name}&7 has been removed from the winner list.\n&7New list: ${Chat.secondaryColor}${
                        winners.joinToString(", ")
                    }"
                )
            } else {
                winners.add(player.name)
                ConfigFeature.instance.data!!.set("game.winners", winners)
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} ${Chat.secondaryColor}${player.name}&7 has been added to the winner list.\n&7New list: ${Chat.secondaryColor}${
                        winners.joinToString(", ")
                    }"
                )
            }
        }
        ConfigFeature.instance.saveData()
        return true
    }

}