package pink.mino.kraftwerk.features

import me.lucko.helper.Schedulers
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.utils.GameState

class StatsFeature : Listener {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity as Player)!!.deaths++
        if (e.entity.killer != null && e.entity.killer.type == EntityType.PLAYER) {
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity.killer as Player)!!.kills++
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        val player = JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.player as Player)!!
        when (e.block.type) {
            Material.DIAMOND_ORE -> {
                player.diamondsMined++
            }
            Material.GOLD_ORE -> {
                player.goldMined++
            }
            Material.IRON_ORE -> {
                player.ironMined++
            }
            else -> {}
        }

    }

    @EventHandler
    fun onConsume(e: PlayerItemConsumeEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.item.type == Material.GOLDEN_APPLE) Schedulers.async().run { JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.player as Player)!!.gapplesEaten++ }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.whoClicked as Player)!!.timesCrafted++
    }

    @EventHandler
    fun onEnchant(e: EnchantItemEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.enchanter as Player)!!.timesEnchanted++
    }
}