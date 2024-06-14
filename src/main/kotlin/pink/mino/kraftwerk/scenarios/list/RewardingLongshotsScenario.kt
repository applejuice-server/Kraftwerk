package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import kotlin.math.floor

class RewardingLongshotsScenario : Scenario(
    "Rewarding Longshots",
    "When shooting and hitting people with an arrow from a variable distance, you will be rewarded with various different items.",
    "rewardinglongshots",
    Material.ARROW
) {
    val prefix = "&8[${Chat.primaryColor}Longshots&8]&7"

    @EventHandler
    fun onPlayerShoot(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player && e.entity.type == EntityType.PLAYER) {
            val distance = (e.damager as Arrow).location.distance((((e.damager as Arrow).shooter) as Player).location)
            val prizes = ArrayList<ItemStack>()
            var broadcast = false
            if (distance > 30 && distance < 50) {
                broadcast = true
                prizes.add(ItemStack(Material.IRON_INGOT))
            } else if (distance > 50 && distance < 100) {
                broadcast = true
                prizes.add(ItemStack(Material.IRON_INGOT))
                prizes.add(ItemStack(Material.GOLD_INGOT))
            } else if (distance > 100 && distance < 200) {
                broadcast = true
                prizes.add(ItemStack(Material.IRON_INGOT))
                prizes.add(ItemStack(Material.GOLD_INGOT))
                prizes.add(ItemStack(Material.DIAMOND))
            } else if (distance > 200) {
                broadcast = true
                prizes.add(ItemStack(Material.IRON_INGOT, 2))
                prizes.add(ItemStack(Material.GOLD_INGOT, 3))
                prizes.add(ItemStack(Material.DIAMOND, 5))
            }
            if (broadcast) Bukkit.broadcastMessage(Chat.colored("$prefix &7${PlayerUtils.getPrefix((((e.damager as Arrow).shooter) as Player))}${(((e.damager as Arrow).shooter) as Player).name}&7 hit ${Chat.secondaryColor}${e.entity.name} from over ${Chat.secondaryColor}${floor(distance)} &7blocks away!"))
            if (prizes.isNotEmpty()) {
                Chat.sendMessage((((e.damager as Arrow).shooter) as Player), prefix + "You got &a${prizes.size} &7prizes!")
                PlayerUtils.bulkItems((((e.damager as Arrow).shooter) as Player), prizes)
            }
        }
    }
}