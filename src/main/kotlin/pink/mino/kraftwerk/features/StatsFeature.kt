package pink.mino.kraftwerk.features

import me.lucko.helper.Schedulers
import me.lucko.helper.promise.Promise
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.StatsHandler

class StatsFeature : Listener {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!JavaPlugin.getPlugin(Kraftwerk::class.java).database) return
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        Schedulers.async().run { StatsHandler.getStatsPlayer(e.entity).add("deaths", 1) }
        if (e.entity.killer != null && e.entity.killer.type == EntityType.PLAYER) {
            Schedulers.async().run { StatsHandler.getStatsPlayer(e.entity.killer as Player).add("kills", 1) }
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!JavaPlugin.getPlugin(Kraftwerk::class.java).database) return
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        Promise.start()
            .thenApplyAsync {
                StatsHandler.getStatsPlayer(e.player)
            }
            .thenAcceptSync {
                when (e.block.type) {
                    Material.DIAMOND_ORE -> {
                        it.add("diamonds_mined", 1)
                    }
                    Material.GOLD_ORE -> {
                        it.add("gold_mined", 1)
                    }
                    Material.IRON_ORE -> {
                        it.add("iron_mined", 1)
                    }
                    else -> {}
                }
            }
    }

    @EventHandler
    fun onConsume(e: PlayerItemConsumeEvent) {
        if (!JavaPlugin.getPlugin(Kraftwerk::class.java).database) return
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        if (e.item.type == Material.GOLDEN_APPLE) Schedulers.async().run { StatsHandler.getStatsPlayer(e.player).add("gapples_eaten", 1) }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (!JavaPlugin.getPlugin(Kraftwerk::class.java).database) return
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        Schedulers.async().run { StatsHandler.getStatsPlayer(e.whoClicked as Player).add("times_crafted", 1) }
    }

    @EventHandler
    fun onEnchant(e: EnchantItemEvent) {
        if (!JavaPlugin.getPlugin(Kraftwerk::class.java).database) return
        if (GameState.currentState != GameState.INGAME) return
        if (ConfigOptionHandler.getOption("statless")!!.enabled) return
        Schedulers.async().run { StatsHandler.getStatsPlayer(e.enchanter).add("times_enchanted", 1) }
    }

    @EventHandler
    fun onDamageByPlayer(e: EntityDamageByEntityEvent) {
        if (!JavaPlugin.getPlugin(Kraftwerk::class.java).database) return
        if (e.damager.type == EntityType.ARROW && (e.damager as Arrow).shooter is Player) {
            if (GameState.currentState != GameState.INGAME) return
            if (ConfigOptionHandler.getOption("statless")!!.enabled) return
            Schedulers.async().run { StatsHandler.getStatsPlayer((e.damager as Arrow).shooter as Player).add("bow_shots") }
        }
    }
}