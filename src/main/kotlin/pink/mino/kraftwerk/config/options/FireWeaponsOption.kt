package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent
import pink.mino.kraftwerk.config.ConfigOption


class FireWeaponsOption : ConfigOption(
    "Fire Weapons",
    "Toggles fire weapons.",
    "options",
    "fireweapons",
    Material.BLAZE_POWDER
) {
    private val fireAspectEnchant: Enchantment = Enchantment.FIRE_ASPECT
    private val flameEnchant: Enchantment = Enchantment.ARROW_FIRE

    @EventHandler
    fun onPlayerEnchant(event: EnchantItemEvent) {
        if (enabled) return
        val enchants: MutableMap<Enchantment, Int> = event.enchantsToAdd
        if (!enchants.containsKey(fireAspectEnchant) || !enchants.containsKey(flameEnchant)) {
            return
        }
        enchants.remove(fireAspectEnchant)
        if (enchants.containsKey(Enchantment.DAMAGE_ALL) || enchants.containsKey(Enchantment.DAMAGE_UNDEAD) || enchants.containsKey(
                Enchantment.DAMAGE_ARTHROPODS
            )
        ) {
            return
        }
        enchants[Enchantment.DAMAGE_ALL] = (event.whichButton() + 1).coerceAtMost(3)
    }
}