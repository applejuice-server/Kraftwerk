package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.GameState


class VeinMinerScenario : Scenario(
    "Vein Miner",
    "Mining an ore while shifting mines the whole vein.",
    "veinminer",
    Material.EMERALD_ORE
) {

    private fun veinMine(loc: Location, material: Material, player: Player) {
        for (x in loc.blockX - 1..loc.blockX + 1) {
            for (y in loc.blockY - 1..loc.blockY + 1) {
                for (z in loc.blockZ - 1..loc.blockZ + 1) {
                    val block = loc.world.getBlockAt(x, y, z)
                    if (material == Material.REDSTONE_ORE || material == Material.GLOWING_REDSTONE_ORE) {
                        if (block.type == Material.REDSTONE_ORE || block.type == Material.GLOWING_REDSTONE_ORE) {
                            block.breakNaturally()
                            val exp = block.location.world.spawn(player.location, ExperienceOrb::class.java)
                            exp.experience = 5
                            block.location.world.playSound(block.location, Sound.DIG_STONE, 1f, 1f)
                            BlockUtil().degradeDurability(player)
                            veinMine(block.location, material, player)
                        }
                    } else {
                        if (block.type == material) {
                            block.breakNaturally()
                            if (material == Material.DIAMOND_ORE || material == Material.COAL_ORE || material == Material.LAPIS_ORE || material == Material.EMERALD_ORE) {
                                val exp = block.location.world.spawn(player.location, ExperienceOrb::class.java)
                                exp.experience = 4
                            } else if (material == Material.GOLD_ORE) {
                                if (ScenarioHandler.getScenario("cutclean")!!.enabled) {
                                    val exp = block.location.world.spawn(player.location, ExperienceOrb::class.java)
                                    exp.experience = 4
                                }
                            } else if (material == Material.IRON_ORE) {
                                if (ScenarioHandler.getScenario("cutclean")!!.enabled) {
                                    val exp = block.location.world.spawn(player.location, ExperienceOrb::class.java)
                                    exp.experience = 3
                                }
                            }
                            block.location.world.playSound(block.location, Sound.DIG_STONE, 1f, 1f)
                            BlockUtil().degradeDurability(player)
                            veinMine(block.location, material, player)
                        }
                    }
                }
            }
        }
    }
    /*
    private fun getBlocks(start: Block, radius: Int, filter: Material): ArrayList<Block> {
        val blocks = ArrayList<Block>()
        var x = start.location.x - radius
        while (x <= start.location.x + radius) {
            var y = start.location.y - radius
            while (y <= start.location.y + radius) {
                var z = start.location.z - radius
                while (z <= start.location.z + radius) {
                    val loc = Location(start.world, x, y, z)
                    if (loc.block.type == filter) blocks.add(loc.block)
                    z++
                }
                y++
            }
            x++
        }
        return blocks
    }
     */

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.player.world.name == "Spawn") return
        if (!e.player.isSneaking) return
        when (e.block.type) {
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE, Material.LAPIS_ORE, Material.QUARTZ_ORE -> {
                veinMine(e.block.location, e.block.type, e.player)
            }
            else -> {}
        }
    }
}