package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import pink.mino.kraftwerk.scenarios.Scenario

class SkyOresScenario : Scenario(
  "Sky Ores",
  "Instead of in caves, ores are in the sky in the inverted way they would be normally. This means diamonds will be high up and coal will be low.",
  "skyores",
  Material.IRON_PICKAXE,
  true
) {
    private val ores: ArrayList<Material> = arrayListOf(
      Material.DIAMOND_ORE,
      Material.EMERALD_ORE,
      Material.GOLD_ORE,
      Material.IRON_ORE,
      Material.LAPIS_ORE,
      Material.REDSTONE_ORE,
      Material.COAL_ORE
    )
    override fun handleBlock(block: Block) {
        if (ores.contains(block.type)) {
            val type = block.type
            block.type = Material.STONE
            val location = Location(block.location.world, block.location.x, 256 - block.location.y, block.location.z)
            location.block.type = type
        }
    }
}