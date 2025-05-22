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
import pink.mino.kraftwerk.utils.PlayerUtils

class ExtremeSkyHighIterator : BukkitRunnable() {
    var timer = 30
    override fun run() {
        timer -= 1
        if (timer == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.location.y < 200) {
                    if (!SpecFeature.instance.getSpecs().contains(player.name) && player.world.name != "Spawn") {
                        player.damage(4.0)
                        Chat.sendMessage(player, "&cYou've been damaged for not being above Y: 200.")
                    }
                }
            }
            timer = 45
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("extremeskyhigh"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
    }
}

class ExtremeSkyHighScenario : Scenario(
    "Extreme SkyHigh",
    "At PvP, if you're not above Y: 200, you will take 2 hearts of damage.",
    "extremeskyhigh",
    Material.FEATHER
) {
    var task: ExtremeSkyHighIterator? = null
    override fun returnTimer(): Int? {
        return if (task != null) {
            task!!.timer
        } else {
            null
        }
    }

    val prefix = "&8[${Chat.primaryColor}Extreme SkyHigh&8]&7"
    override fun onPvP() {
        task = ExtremeSkyHighIterator()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        Bukkit.broadcastMessage(Chat.colored("${prefix} The damage tick for ${Chat.secondaryColor}Extreme SkyHigh&7 has started, the damage tick happen every 45 seconds."))
    }

    override fun onStart() {
        val shovel = ItemStack(Material.DIAMOND_SPADE)
        val meta = shovel.itemMeta
        meta.spigot().isUnbreakable = true
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true)
        shovel.itemMeta = meta
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                PlayerUtils.bulkItems(player, arrayListOf(ItemStack(Material.STAINED_CLAY, 128, 14), ItemStack(Material.PUMPKIN, 2), ItemStack(Material.SNOW_BLOCK, 4), ItemStack(Material.STRING, 2), shovel, ItemStack(Material.FEATHER, 16)))
                Chat.sendMessage(player, "${prefix} You've been given your SkyHigh items.")
            }
        }
    }

    override fun givePlayer(player: Player) {
        val shovel = ItemStack(Material.DIAMOND_SPADE)
        val meta = shovel.itemMeta
        meta.spigot().isUnbreakable = true
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true)
        shovel.itemMeta = meta
        PlayerUtils.bulkItems(player, arrayListOf(ItemStack(Material.STAINED_CLAY, 128, 14), ItemStack(Material.PUMPKIN, 2), ItemStack(Material.SNOW_BLOCK, 4), ItemStack(Material.STRING, 2), shovel, ItemStack(Material.FEATHER, 16)))
        Chat.sendMessage(player, "${prefix} You've been given your SkyHigh items.")
    }
}