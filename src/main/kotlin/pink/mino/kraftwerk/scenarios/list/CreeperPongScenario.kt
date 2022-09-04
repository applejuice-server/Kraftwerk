package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.material.SpawnEgg
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.PlayerUtils

class CreeperPongScenario : Scenario(
    "Creeper Pong",
    "At the start of the game, you receive 64 creeper spawn eggs & a knockback stick.",
    "creeperpong",
    Material.SULPHUR
) {
    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                val spawnEgg = SpawnEgg(EntityType.CREEPER).toItemStack()
                spawnEgg.amount = 64
                val stick = ItemBuilder(Material.STICK)
                    .name("&cKnockybacky Stick")
                    .addEnchantment(Enchantment.KNOCKBACK, 10)
                    .make()
                val list = arrayListOf(
                    stick,
                    spawnEgg
                )
                PlayerUtils.bulkItems(player, list)
            }
        }
    }

    override fun givePlayer(player: Player) {
        val spawnEgg = SpawnEgg(EntityType.CREEPER).toItemStack()
        spawnEgg.amount = 64
        val stick = ItemBuilder(Material.STICK)
            .name("&cKnockybacky Stick")
            .addEnchantment(Enchantment.KNOCKBACK, 10)
            .make()
        val list = arrayListOf(
            stick,
            spawnEgg
        )
        PlayerUtils.bulkItems(player, list)
    }
}