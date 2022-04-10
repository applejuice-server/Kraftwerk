package pink.mino.kraftwerk.features

import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.StatsHandler

class StatsFeature : Listener {

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        StatsHandler.getStatsPlayer(e.entity).add("deaths", 1)
        if (e.entity.killer.type == EntityType.PLAYER) {
            StatsHandler.getStatsPlayer(e.entity.killer).add("kills", 1)
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        val player = StatsHandler.getStatsPlayer(e.player)
        when (e.block.type) {
            Material.DIAMOND_ORE -> {
                player.add("diamonds_mined", 1)
            }
            Material.GOLD_ORE -> {
                player.add("gold_mined", 1)
            }
            Material.IRON_ORE -> {
                player.add("iron_mined", 1)
            }
            else -> {}
        }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (GameState.currentState != GameState.INGAME) return
        StatsHandler.getStatsPlayer(e.whoClicked as Player).add("times_crafted", 1)
    }

    @EventHandler
    fun onEnchant(e: EnchantItemEvent) {
        if (GameState.currentState != GameState.INGAME) return
        StatsHandler.getStatsPlayer(e.enchanter).add("times_enchanted", 1)
    }
}