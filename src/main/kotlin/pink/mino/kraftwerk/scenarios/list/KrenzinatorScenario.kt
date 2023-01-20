package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Egg
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class KrenzinatorScenario : Scenario(
    "Krenzinator",
    "9 redstone blocks can be crafted into 1 diamond, horses are disabled (but donkeys aren't), crafting a diamond sword will result in a loss of 1 heart, eggs do damage in this gamemode (0.5 hearts), nether is off.",
    "krenzinator",
    Material.REDSTONE_BLOCK
) {
    val prefix: String = "&8[&cKrenzinator&8]&7"

    override fun onToggle(to: Boolean) {
        if (!to) {
            Bukkit.getServer().resetRecipes()
            JavaPlugin.getPlugin(Kraftwerk::class.java).addRecipes()
        } else {
            val diamond = ShapelessRecipe(ItemStack(Material.DIAMOND))
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
                .addIngredient(Material.REDSTONE_BLOCK)
            Bukkit.getServer().addRecipe(diamond)
        }
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        if (!enabled) return
        val player = e.whoClicked as Player
        val inv = e.inventory
        val item = inv.result
        if (item.type == Material.DIAMOND_SWORD) {
            player.damage(2.0)
            Chat.sendMessage(player, "$prefix Krenzinator doesn't like diamond swords, therefore crafting a diamond sword will result in a loss of 1 heart")
        }
    }

    @EventHandler
    fun onPlayerThrowEgg(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (e.damager is Egg) {
            if (e.entity is Player) e.damage = 1.0
        }
    }
}