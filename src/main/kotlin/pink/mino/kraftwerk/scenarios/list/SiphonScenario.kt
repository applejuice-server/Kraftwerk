package pink.mino.kraftwerk.scenarios.list

import com.google.common.collect.ImmutableList
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import java.util.*


class SiphonScenario : Scenario(
  "Siphon",
  "Whenever you get a kill, you will regenerate 2 hearts, gain 2 levels and get a random tier 1 enchanted book",
  "siphon",
  Material.POTION

) {
    val enchants: ImmutableList<Enchantment> = ImmutableList.of(
        Enchantment.DIG_SPEED,
        Enchantment.DURABILITY,
        Enchantment.PROTECTION_PROJECTILE,
        Enchantment.PROTECTION_ENVIRONMENTAL,
        Enchantment.FIRE_ASPECT,
        Enchantment.DAMAGE_ALL
    )

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (!enabled) return
        val killer: Player = event.entity.killer ?: return
        killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 1, true, true))
        val item = ItemStack(Material.ENCHANTED_BOOK)
        val meta = item.itemMeta as BookMeta
        meta.addEnchant(enchants[Random().nextInt(enchants.size)], Random().nextInt(2), true)
        killer.inventory.addItem(item)
        killer.level = killer.level + 2
    }

}