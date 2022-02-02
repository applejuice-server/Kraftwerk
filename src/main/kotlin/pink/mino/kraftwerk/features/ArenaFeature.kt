package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.Chat

class ArenaFeature : Listener {

    fun send(p: Player) {
        p.health = 20.0
        p.foodLevel = 20
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null

        p.inventory.setItem(0, ItemStack(Material.DIAMOND_SWORD))
        p.inventory.setItem(1, ItemStack(Material.FISHING_ROD))
        p.inventory.setItem(2, ItemStack(Material.BOW))
        p.inventory.setItem(3, ItemStack(Material.COBBLESTONE, 64))
        p.inventory.setItem(4, ItemStack(Material.WATER_BUCKET))
        p.inventory.setItem(5, ItemStack(Material.LAVA_BUCKET))
        p.inventory.setItem(6, ItemStack(Material.GOLDEN_CARROT, 16))
        p.inventory.setItem(7, ItemStack(Material.GOLDEN_APPLE, 5))
        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 3)
        val meta = goldenHeads.itemMeta
        meta.displayName = Chat.colored("&5Golden Head")
        goldenHeads.itemMeta = meta
        p.inventory.setItem(8, goldenHeads)

        p.inventory.helmet = ItemStack(Material.IRON_HELMET)
        p.inventory.chestplate = ItemStack(Material.IRON_CHESTPLATE)
        p.inventory.leggings = ItemStack(Material.IRON_LEGGINGS)
        p.inventory.boots = ItemStack(Material.DIAMOND_BOOTS)

        ScatterFeature.scatterSolo(p, Bukkit.getWorld("Arena"), 100)
        p.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 100, true, false))
        Chat.sendMessage(p, "${Chat.prefix} Welcome to the arena, &f${p.name}&7!")
        Chat.sendMessage(p, "&8(&7Cross-teaming in the arena is not allowed!&8)")
    }

    @EventHandler
    fun onAnimalSpawn(e: EntitySpawnEvent) {
        if (e.location.world.name != "Arena") {
            return
        }
        when (e.entityType) {
            EntityType.CHICKEN -> {
                e.isCancelled = true
            }
            EntityType.HORSE -> {
                e.isCancelled = true
            }
            EntityType.COW -> {
                e.isCancelled = true
            }
            EntityType.SHEEP -> {
                e.isCancelled = true
            }
            EntityType.OCELOT -> {
                e.isCancelled = true
            }
            EntityType.PIG -> {
                e.isCancelled = true
            }
            EntityType.WOLF -> {
                e.isCancelled = true
            }
            EntityType.MUSHROOM_COW -> {
                e.isCancelled = true
            }
            else -> {}
        }
    }
}