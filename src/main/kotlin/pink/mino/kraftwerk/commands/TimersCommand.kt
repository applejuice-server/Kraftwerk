package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.MiscUtils

class TimersCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
            Chat.sendMessage(sender, "&cNo game is running!")
            return true
        }
        val valid = arrayListOf<Scenario>()
        Chat.sendMessage(sender, Chat.line)
        Chat.sendCenteredMessage(sender, "&c&lScenario Timers")
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            if (scenario.returnTimer() != null) {
                valid.add(scenario)
                Chat.sendCenteredMessage(sender, "&7${scenario.name} &8- &f${MiscUtils.timeToString(scenario.returnTimer()!!.toLong())}")
            }
        }
        if (valid.isEmpty()) {
            Chat.sendCenteredMessage(sender, "&cNo valid scenarios with timers are running.")
        }
        Chat.sendMessage(sender, Chat.line)
        return true
    }

}