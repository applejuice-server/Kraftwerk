package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class UnendurableHealingScenario : Scenario(
    "Unendurable Healing",
    "Every time you heal, your armour, pick, sword, etc. (anything with durability) will lose 20% of their durability.",
    "unendurablehealing",
    Material.CHAINMAIL_CHESTPLATE
){
    @EventHandler
    fun onPlayerHeal(event: PlayerItemConsumeEvent){
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (event.item.type == Material.GOLDEN_APPLE) return
        val player = event.player
        val inventory = player.inventory
        for (item in inventory.contents) {
            if (item != null) {
                if (item.type.maxDurability != 0.toShort()){
                    item.durability = (item.durability + (item.type.maxDurability * 0.2)).toInt().toShort()
                }
            }
        }
        if (inventory.helmet != null) {
            if (inventory.helmet!!.type.maxDurability != 0.toShort()){
                inventory.helmet!!.durability = (inventory.helmet!!.durability + (inventory.helmet!!.type.maxDurability * 0.2)).toInt().toShort()
            }
        }
        if (inventory.chestplate != null) {
            if (inventory.chestplate!!.type.maxDurability != 0.toShort()){
                inventory.chestplate!!.durability = (inventory.chestplate!!.durability + (inventory.chestplate!!.type.maxDurability * 0.2)).toInt().toShort()
            }
        }
        if (inventory.leggings != null) {
            if (inventory.leggings!!.type.maxDurability != 0.toShort()){
                inventory.leggings!!.durability = (inventory.leggings!!.durability + (inventory.leggings!!.type.maxDurability * 0.2)).toInt().toShort()
            }
        }
        if (inventory.boots != null) {
            if (inventory.boots!!.type.maxDurability != 0.toShort()){
                inventory.boots!!.durability = (inventory.boots!!.durability + (inventory.boots!!.type.maxDurability * 0.2)).toInt().toShort()
            }
        }
        player.playSound(player.location, Sound.ITEM_BREAK, 1F, 1F)
    }
}