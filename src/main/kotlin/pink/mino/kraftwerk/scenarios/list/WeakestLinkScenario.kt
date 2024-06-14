package pink.mino.kraftwerk.scenarios.list

import com.google.common.collect.Ordering
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat


class WeakestLinkLogic : BukkitRunnable() {
    var timer = 600
    val prefix = "&8[${Chat.primaryColor}Weakest Link&8]&7"
    override fun run() {
        if (timer == 0) {
            timer = 600
            if (WeakestLinkScenario().isAllSameHealth()) {
                Bukkit.broadcastMessage(Chat.colored("$prefix ${Chat.secondaryColor}Everyone&7 was spared for now."))
            } else {
                val player = WeakestLinkScenario().getLowestHealth()
                player.damage(999999.9)
                Bukkit.broadcastMessage(Chat.colored("$prefix ${Chat.secondaryColor}${player.name}&7 was the &eWeakest Link&7!"))
            }
        }
        timer -= 1
    }
}

class WeakestLinkScenario : Scenario(
    "Weakest Link",
    "Every 10 minutes, the person with the least health will be killed.",
    "weakestlink",
    Material.IRON_BARDING
) {
    private val byHealth: Ordering<Player?> = object : Ordering<Player?>() {
        override fun compare(p0: Player?, p1: Player?): Int {
            return p0!!.health.compareTo(p1!!.health)
        }
    }
    var task: WeakestLinkLogic? = null

    fun getLowestHealth() : Player {
        val players = arrayListOf<Player>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                players.add(player)
            }
        }
        return byHealth.min(players)
    }

    override fun returnTimer(): Int? {
        if (this.task == null) return null
        return this.task!!.timer
    }

    fun isAllSameHealth() : Boolean {
        val players = arrayListOf<Player>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                players.add(player)
            }
        }
        var last: Player? = null
        for (player in players) {
            if (last != null && last.health != player.health) {
                return false
            }
            last = player
        }
        return true
    }

    override fun onStart() {
        task = WeakestLinkLogic()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
    }
}