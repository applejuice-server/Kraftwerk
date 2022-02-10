package pink.mino.kraftwerk.features

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.inventory.ItemStack
import java.util.*


class RatesFeature : Listener {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.block.type == Material.GRAVEL) {
            if ((Random().nextDouble() * 100) < SettingsFeature.instance.data!!.getInt("game.rates.flint")) {
                e.block.type = Material.AIR
                e.block.drops.clear()
                e.block.world.dropItemNaturally(e.block.location.add(0.5, 0.5, 0.5), ItemStack(Material.FLINT))
            }
        } else if (e.block.type == Material.LEAVES || e.block.type == Material.LEAVES_2) {
            if ((Random().nextDouble() * 100) < SettingsFeature.instance.data!!.getInt("game.rates.apple")) {
                e.block.type = Material.AIR
                e.block.drops.clear()
                e.block.world.dropItem(e.block.location, ItemStack(Material.APPLE))
            }
        }
    }

    @EventHandler
    fun onLeavesDecay(e: LeavesDecayEvent) {
        if (e.block.type == Material.LEAVES || e.block.type == Material.LEAVES_2) {
            if ((Random().nextDouble() * 100) < SettingsFeature.instance.data!!.getInt("game.rates.apple")) {
                e.block.type = Material.AIR
                e.block.drops.clear()
                e.block.world.dropItem(e.block.location, ItemStack(Material.APPLE))
            }
        }
    }
}