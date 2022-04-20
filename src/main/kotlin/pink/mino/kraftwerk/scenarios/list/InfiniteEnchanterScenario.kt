package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PlayerUtils

class InfiniteEnchanterScenario : Scenario(
    "Infinite Enchanter",
    "Players are given items for enchanting at the beginning of the game.",
    "infiniteenchanter",
    Material.EXP_BOTTLE
) {
    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                val list = arrayListOf(
                    ItemStack(Material.ENCHANTMENT_TABLE, 2),
                    ItemStack(Material.BOOKSHELF, 64),
                    ItemStack(Material.LAPIS_BLOCK, 64),
                    ItemStack(Material.EXP_BOTTLE, 128),
                )
                PlayerUtils.bulkItems(player, list)
                Chat.sendMessage(player, "${Chat.prefix} You've been given your &cInfinite Enchanter&7 items.")
            }
        }
    }

    override fun givePlayer(player: Player) {
        val list = arrayListOf(
            ItemStack(Material.ENCHANTMENT_TABLE, 2),
            ItemStack(Material.BOOKSHELF, 64),
            ItemStack(Material.LAPIS_BLOCK, 64),
            ItemStack(Material.EXP_BOTTLE, 128),
        )
        PlayerUtils.bulkItems(player, list)
        Chat.sendMessage(player, "${Chat.prefix} You've been given your &cInfinite Enchanter&7 items.")
    }
}