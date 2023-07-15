package pink.mino.kraftwerk.features

import me.lucko.helper.promise.Promise
import me.lucko.helper.utils.Log
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import kotlin.random.Random

class XpFeature : Listener {

    fun add(p: OfflinePlayer, amount: Double) {
        Promise.start()
            .thenApplySync {
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId)
            }
            .thenAcceptSync {
                it.get().xp += amount
                if (it.get().xp >= it.get().xpNeeded) {
                    it.get().level++
                    it.get().xpNeeded *= 1.15
                    it.get().xp -= it.get().xpNeeded
                    Log.info("${p.name} leveled up to Level ${it.get().level}")
                    val reward = Random.nextInt(25, 75)
                    it.get().coins += reward
                    Chat.sendMessage(p as CommandSender, "&8[&bLeveling&8]&7 You leveled up to &bLevel ${it.get().level}&7! You gained &6⚜ ${(reward)}&7 as a reward.")
                }
            }
    }
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.entity.killer != null && e.entity.killer.type == EntityType.PLAYER) {
            add(e.entity.killer as Player, 15.0)
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        when (e.block.type) {
            Material.DIAMOND_ORE -> {
                if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("flowerpower")) || ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("undergroundparallel"))) {
                    add(e.player, 2.5)
                } else {
                    add(e.player, 5.0)
                }
            }
            Material.GOLD_ORE -> {
                if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("flowerpower")) || ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("undergroundparallel"))) {
                    add(e.player, 1.5)
                } else {
                    add(e.player, 3.0)
                }
            }
            else -> {}
        }
    }
}