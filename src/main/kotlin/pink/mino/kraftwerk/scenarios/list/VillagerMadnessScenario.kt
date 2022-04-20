package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.SpawnEgg
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.PlayerUtils

class VillagerMadnessScenario : Scenario(
    "Villager Madness",
    "You are given a stack of emerald blocks & villager spawn eggs at the start of the game.",
    "villagermadness",
    Material.EMERALD
) {
    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                val list = arrayListOf(
                    ItemStack(Material.EMERALD_BLOCK, 64),
                    SpawnEgg(EntityType.VILLAGER).toItemStack()
                )
                PlayerUtils.bulkItems(player, list)
            }
        }
    }

    override fun givePlayer(player: Player) {
        val list = arrayListOf(
            ItemStack(Material.EMERALD_BLOCK, 64),
            SpawnEgg(EntityType.VILLAGER).toItemStack()
        )
        PlayerUtils.bulkItems(player, list)
    }
}