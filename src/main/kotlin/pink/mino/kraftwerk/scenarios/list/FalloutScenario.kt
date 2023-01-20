package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class FalloutIterator : BukkitRunnable() {
    var timer = 45
    override fun run() {
        timer -= 1
        if (timer == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.location.y > 60) {
                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                        player.damage(2.0)
                        Chat.sendMessage(player, "&cYou've been damaged for not being below y-60.")
                    }
                }
            }
            timer = 45
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("fallout"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
    }
}

class FalloutScenario : Scenario(
    "Fallout",
    "If you are not below y-60 at PvP, you will take damage.",
    "fallout",
    Material.SPIDER_EYE
) {
    var task: FalloutIterator? = null

    override fun returnTimer(): Int? {
        return if (task != null) {
            task!!.timer
        } else {
            null
        }
    }

    val prefix = "&8[&cFallout&8]&7"
    override fun onPvP() {
        task = FalloutIterator()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        Bukkit.broadcastMessage(Chat.colored("${prefix} The damage tick for Fallout has started, the damage tick happen every 45 seconds."))
    }
}