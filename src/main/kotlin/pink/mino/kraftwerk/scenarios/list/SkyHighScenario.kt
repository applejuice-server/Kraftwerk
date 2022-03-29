package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class SkyHighIterator : BukkitRunnable() {
    var timer = 45
    override fun run() {
        timer -= 1
        if (timer == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.location.y < 100) {
                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                        player.damage(2.0)
                        Chat.sendMessage(player, "&cYou've been damaged for not being above y-101.")
                    }
                }
            }
            timer = 45
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("skyhigh"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
    }

}

class SkyHighScenario : Scenario(
    "SkyHigh",
    "All players must go above y-101 when PvP occurs, lest they take damage.",
    "skyhigh",
    Material.FEATHER
) {
    override fun onStart() {
        val shovel = ItemStack(Material.DIAMOND_SPADE)
        val meta = shovel.itemMeta
        meta.spigot().isUnbreakable = true
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true)
        shovel.itemMeta = meta
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                player.inventory.addItem(ItemStack(Material.STAINED_CLAY, 128, 14), ItemStack(Material.PUMPKIN, 2), ItemStack(Material.SNOW_BLOCK, 4), ItemStack(Material.STRING, 2), shovel, ItemStack(Material.FEATHER, 16))
                Chat.sendMessage(player, "${Chat.prefix} You've been given your SkyHigh items.")
            }
        }
    }

    override fun onPvP() {
        SkyHighIterator().runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The damage tick for SkyHigh has started, the damage tick happen every 45 seconds."))
    }

    override fun givePlayer(player: Player) {
        val shovel = ItemStack(Material.DIAMOND_SPADE)
        val meta = shovel.itemMeta
        meta.spigot().isUnbreakable = true
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true)
        shovel.itemMeta = meta
        player.inventory.addItem(ItemStack(Material.STAINED_CLAY, 128, 14), ItemStack(Material.PUMPKIN, 2), ItemStack(Material.SNOW_BLOCK, 4), ItemStack(Material.STRING, 2), shovel, ItemStack(Material.FEATHER, 16))
        Chat.sendMessage(player, "${Chat.prefix} You've been given your SkyHigh items.")
    }
}