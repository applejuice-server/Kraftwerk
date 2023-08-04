package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import kotlin.random.Random

class PlayerSwapTimer : BukkitRunnable() {
    var timer = 60 * (Random.nextInt(2, 5))

    override fun run() {
        timer -= 1
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("playerswap"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
        if (timer == 0) {
            timer = 60 * (Random.nextInt(2, 6))
            val pool = ArrayList<Player>()
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.isSpec(player)) {
                    pool.add(player)
                }
            }
            val player1 = pool.random()
            pool.remove(player1)
            val player2 = pool.random()
            val loc1 = player1.location
            val loc2 = player2.location
            player1.teleport(loc2)
            player2.teleport(loc1)
            Chat.broadcast("${PlayerSwapScenario.prefix} &f${PlayerUtils.getPrefix(player1)}${player1.name}&7 has swapped with &f${PlayerUtils.getPrefix(player2)}${player2.name}&7! View &f/timer&7 for the next swap.")
        }
    }
}

class PlayerSwapScenario : Scenario(
  "Player Swap",
  "Every 2 to 5 minutes, at an unannounced time, two players will swap places.",
  "playerswap",
  Material.ENDER_PEARL
) {
    companion object {
        var task: PlayerSwapTimer? = null
        var prefix = "&8[&cPlayer Swap&8]&7"
    }

    override fun onStart() {
        task = PlayerSwapTimer()
        task!!.runTaskTimer(Kraftwerk.instance, 0L, 20L)
    }

    override fun returnTimer(): Int? {
        return if (task != null) {
            task!!.timer
        } else {
            null
        }
    }
}