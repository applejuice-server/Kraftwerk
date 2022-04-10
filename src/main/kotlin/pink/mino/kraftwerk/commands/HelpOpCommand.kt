package pink.mino.kraftwerk.commands

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.HelpOp


class HelpOpCommand : CommandExecutor {
    var cooldowns = HashMap<String, Long>()

    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Now, why would *you* need to use this command?")
            return false
        }
        val cooldownTime = 60 // Get number of seconds from wherever you want
        if (cooldowns.containsKey(sender.name)) {
            val secondsLeft: Long = cooldowns[sender.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
            if (secondsLeft > 0) {
                sender.sendMessage(Chat.colored("&cYou can't use this command for another $secondsLeft second(s)!"))
                return false
            }
        }
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: /helpop <message>")
            return true
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "&cYou can only use this command during a game.")
            return false
        }
        val message = StringBuilder()
        for (element in args) {
            message.append("${ChatColor.GRAY}${element}").append(" " + ChatColor.GRAY)
        }
        val msg = message.toString().trim()
        val id = HelpOp.addHelpop(sender)
        Chat.sendMessage(sender, "&8[&4Help-OP&8]&7 Successfully sent your &chelp-op&7, please wait for someone to answer it!")
        val text = TextComponent(Chat.colored("&8[&4Help-OP&8] &8[&c#${id}&8] &f${sender.name} ${Chat.dash}&7 $msg"))
        text.clickEvent = ClickEvent(
            ClickEvent.Action.SUGGEST_COMMAND,
            "/hr $id "
        )
        for (name in SpecFeature.instance.getSpecs()) {
            val player = Bukkit.getPlayer(name)
            player?.spigot()?.sendMessage(text)
        }
        cooldowns[sender.name] = System.currentTimeMillis()
        return true
    }
}