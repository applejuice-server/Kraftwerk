package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Chicken
import org.bukkit.entity.Egg
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class EggsScenario : Scenario(
    "Eggs",
    "When you throw an egg, a random mob will spawn where it lands.",
    "eggs",
    Material.EGG
) {

    private var canUse = false

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                player.inventory.addItem(ItemStack(Material.EGG, 5))
            }
        }
        canUse = false
    }

    override fun onPvP() {
        canUse = true
    }

    override fun givePlayer(player: Player) {
        player.inventory.addItem(ItemStack(Material.EGG, 5))
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (!enabled) return
        if (GameState.currentState !== GameState.INGAME) return
        if (event.entity !is Chicken) return

        if (Random().nextDouble() > 0.05) return
        event.drops.add(ItemStack(Material.EGG, 1))
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        if (!enabled) return
        if (GameState.currentState !== GameState.INGAME) return
        if (event.item == null) return
        if (event.item.type != Material.EGG) return
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) return
        if (canUse) return
        event.isCancelled = true
        Chat.sendMessage(event.player, "&cYou can't use eggs until PvP.")
    }

    @EventHandler
    fun onProjHit(event: ProjectileHitEvent) {
        if (!enabled) return
        if (GameState.currentState !== GameState.INGAME) return
        if (event.entity !is Egg) return

        val rand = Random()

        val types: ArrayList<EntityType> = arrayListOf()

        for (type in EntityType.values()) {
            if (!type.isAlive || !type.isSpawnable) continue
            types.add(type)
        }

        val entityType = types[rand.nextInt(types.size)]

        val loc = event.entity.location
        val world = event.entity.world

        val gameWorld = Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world"))

        if (world != gameWorld) return

        world.spawnEntity(loc, entityType)
    }

}