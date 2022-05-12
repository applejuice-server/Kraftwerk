package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class VoteTimer(private val vote: Vote) : BukkitRunnable() {
    private var timer = 15
    override fun run() {
        timer -= 1
        if (timer == 0) {
            if (vote.yes > vote.no) {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &aYes&7 wins with &a${vote.yes} yes(es)&7! &8| &7Question: &c${vote.question}"))
            } else if (vote.no > vote.yes) {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &cNo&7 wins with &c${vote.no} no(s)&7! &8| &7Question: &c${vote.question}"))
            } else {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &7It's a draw! &8| &7Question: &c${vote.question}"))
            }
            JavaPlugin.getPlugin(Kraftwerk::class.java).vote = null
            cancel()
        }
    }
}

class Vote(val question: String) {
    var yes = 0
    var no = 0
    var voted = arrayListOf<Player>()

    fun startTimer() {
        VoteTimer(this).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Poll: &c${question} &8|&7 Use &a/yes &7or &c/no&7 to respond."))
    }
}

class StartVoteCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        val message = StringBuilder()
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: /startvote <question>")
            return true
        }
        for (element in args) {
            message.append(element).append(" ")
        }
        val question = message.toString().trim()
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).vote != null) {
            Chat.sendMessage(sender, "&cThere's already a poll currently running.")
            return false
        }
        Chat.sendMessage(sender, "${Chat.prefix} Starting poll...")
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote = Vote(question)
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.startTimer()
        return true
    }
}