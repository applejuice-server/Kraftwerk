package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*


class KillTopCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "&cYou can't use this command right now.")
            return false
        }
        if (ConfigFeature.instance.data!!.get("game.kills") == null) {
            Chat.sendMessage(sender, "&cThere are no kills yet.")
            return false
        }
        val map = LinkedHashMap<String, Int>()
        for (key in ConfigFeature.instance.data!!.getConfigurationSection("game.kills").getKeys(false)) {
            map[key] = ConfigFeature.instance.data!!.getInt("game.kills.${key}")
        }
        val entries: List<Map.Entry<String, Int>> = ArrayList<Map.Entry<String, Int>>(map.entries)
        Collections.sort(entries
        ) { o1, o2 -> o1!!.value - o2!!.value }
        Collections.reverse(entries)
        Chat.sendMessage(sender, Chat.line)
        Chat.sendCenteredMessage(sender, "${Chat.primaryColor}&lKill Leaderboard")
        Chat.sendMessage(sender , " ")
        for ((key, value) in entries) {
            Chat.sendCenteredMessage(sender, "&7${key} ${Chat.dash} ${Chat.secondaryColor}${value}")
        }
        Chat.sendMessage(sender, Chat.line)
        return true
    }
}