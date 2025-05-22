package pink.mino.kraftwerk.listeners.donator

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerEggThrowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*

class MobEggsListener : Listener{

    private fun noAI(bukkitEntity: Entity) {
        val craftEntity = bukkitEntity as CraftEntity
        val nmsEntity: net.minecraft.server.v1_8_R3.Entity = craftEntity.handle
        var tag: net.minecraft.server.v1_8_R3.NBTTagCompound? = nmsEntity.nbtTag
        if (tag == null) {
            tag = net.minecraft.server.v1_8_R3.NBTTagCompound()
        }
        nmsEntity.c(tag)
        tag.setInt("NoAI", 1)
        nmsEntity.f(tag)
    }

    @EventHandler
    fun onEggHit(event: ProjectileHitEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.entity !is Egg) return
        if (event.entity.shooter !is Player) return

        val player = event.entity.shooter as Player
        if (!PerkChecker.checkPerk(player, "mobEggs")) return

        val rand = Random()

        val types: ArrayList<EntityType> = arrayListOf()

        for (type in EntityType.values()) {
            if (!type.isAlive || !type.isSpawnable) continue
            types.add(type)
        }

        val entityType = types[rand.nextInt(types.size)]

        val loc = event.entity.location
        val world = event.entity.world

        if (world.name != "Spawn") return

        // find a better way to find a spawn loc, because with no ai, it'll just fly in the air where it lands
        val entity = world.spawnEntity(LocationUtils.getHighestBlock(loc).add(0.0, 1.0, 0.0), entityType)
        entity.customName = Chat.colored("&6${player.name}'s &e${entityType.name}")
        if (entity is LivingEntity) {
            noAI(entity)
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            entity.remove()
        }, 20 * 10)
    }

    @EventHandler
    fun onEggThrow(event: PlayerEggThrowEvent) {
        if (GameState.currentState == GameState.INGAME) return
        event.isHatching = false
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.item == null) return
        if (event.item.type != Material.EGG) return
        val player = event.player
        if (!PerkChecker.checkPerk(player, "mobEggs")) {
            player.sendMessage(Chat.colored("&cYou do not have the Mob Eggs perk, buy a rank on the store at &eapplejuice.tebex.io&c!"))
            event.isCancelled = true
            return
        }

        if (event.item.amount == 1) {
            Chat.sendMessage(player, "&8[&2$$$&8] &7You have no more mob eggs! Giving you more in &c10 seconds&7!")
            val mobEggs = ItemBuilder(Material.EGG)
                .name("&2Mob Eggs &7(Throw)")
                .addLore("&7Throw these eggs in the Spawn to spawn mobs!")
                .addLore("&7&oMobs last for 10 seconds.")
                .addLore("&7&oEggs replenish after 10 seconds of no eggs.")
                .setAmount(5)
                .make()
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                if (player.isOnline && player.world.name == "Spawn") {
                    player.inventory.setItem(7, mobEggs)
                }
            }, 20 * 10)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.entity is Player) return
        if (event.entity.world.name != "Spawn") return
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.entity.world.name != "Spawn") return
        if (event.entity is Player) return
        event.isCancelled = true
    }

}