package pink.mino.kraftwerk.features

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.lunarclient.bukkitapi.LunarClientAPI
import me.lucko.helper.Schedulers
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor

class InvSeeFeature(private val player: Player, private val target: Player, ) : BukkitRunnable() {
    override fun run() {
        if (!target.isOnline) {
            cancel()
            return
        }
        if (player.openInventory.title != "${target.name}'s Inventory") {
            cancel()
            return
        }
        for ((index, item) in target.inventory.contents.withIndex()) {
            if (item == null) {
                player.openInventory.topInventory.setItem(index, ItemStack(Material.AIR))
            } else {
                player.openInventory.topInventory.setItem(index, item)
            }
        }

        if (target.inventory.helmet != null) player.openInventory.topInventory.setItem(38, target.inventory.helmet)
        if (target.inventory.chestplate != null) player.openInventory.topInventory.setItem(39, target.inventory.chestplate)
        if (target.inventory.leggings != null) player.openInventory.topInventory.setItem(41, target.inventory.leggings)
        if (target.inventory.boots != null) player.openInventory.topInventory.setItem(42, target.inventory.boots)
    }
}

class SpecClickFeature : PacketAdapter(JavaPlugin.getPlugin(Kraftwerk::class.java), ListenerPriority.MONITOR, PacketType.Play.Client.WINDOW_CLICK) {
    override fun onPacketReceiving(e: PacketEvent) {
        if (e.packetType.equals(PacketType.Play.Client.WINDOW_CLICK)) {
            val packet = e.packet
            if (SpecFeature.instance.isSpec(e.player)) {
                val p = e.player
                when (packet.integers.read(1)) {
                    19 -> {
                        p.teleport(Location(p.world, 0.0, 100.0, 0.0))
                        Chat.sendMessage(p, "${Chat.prefix} You have been teleported to 0,0.")
                    }
                    21 -> {
                        Bukkit.dispatchCommand(p, "nearby")
                    }
                    23 -> {
                        val list = ArrayList<Player>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player != p && !SpecFeature.instance.isSpec(player)) {
                                list.add(player)
                            }
                        }
                        if (list.isEmpty()) {
                            Chat.sendMessage(p, "${Chat.prefix} There are no players nearby.")
                            return
                        }
                        Chat.sendMessage(p, Chat.line)
                        Chat.sendCenteredMessage(p, "&c&lPlayer Locations")
                        for (player in list) {
                            Chat.sendMessage(
                                p,
                                "${Chat.prefix} &7${player.name} &7is at &b${floor(player.location.x)}, &7${floor(player.location.y)}, &7${
                                    floor(player.location.z)
                                }"
                            )
                        }
                        Chat.sendMessage(p, Chat.line)
                    }
                    25 -> {
                        Bukkit.dispatchCommand(p, "respawn")
                    }
                    else -> { return }
                }
            }
        }
    }
}


class SpecFeature : Listener {
    val diamondsMined: HashMap<UUID, Int> = HashMap()
    val goldMined: HashMap<UUID, Int> = HashMap()

    companion object {
        val instance = SpecFeature()
    }
    val prefix = "&8[&4Spec&8]&7"

    fun spec(p: Player) {
        SpawnFeature.instance.send(p)
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null
        p.gameMode = GameMode.SPECTATOR

        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list.contains(p.name)) list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()

        specChat("&f${p.name}&7 has entered spectator mode.", p)

        val teleportTo00 = ItemBuilder(Material.EYE_OF_ENDER)
            .name("&cTeleport to 0,0")
            .addLore("&7Click the item to teleport yourself to &c0,0&7.")
            .make()
        val nearby = ItemBuilder(Material.COMPASS)
            .name("&cNearby Players")
            .addLore("&7Click the item to see a list of nearby players.")
            .make()
        val locations = ItemBuilder(Material.MAP)
            .name("&cPlayer Locations")
            .addLore("&7Click the item to see a list of player locations.")
            .make()
        val respawn = ItemBuilder(Material.BONE)
            .name("&cRespawn Players")
            .addLore("&7Click to view a list of dead players that can be respawned.")
            .make()

        p.inventory.setItem(19, teleportTo00)
        p.inventory.setItem(21, nearby)
        p.inventory.setItem(23, locations)
        p.inventory.setItem(25, respawn)

        Chat.sendMessage(p, "${Chat.prefix} You are now in spectator mode.")
        Chat.sendMessage(p, "${Chat.dash} &7Your &bLunar Client&7 staff modules have been enabled.")

        if (LunarClientAPI.getInstance().isRunningLunarClient(p)) {
            LunarClientAPI.getInstance().giveAllStaffModules(p)
        }
    }

    fun unspec(p: Player) {
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.armorContents = null

        SpawnFeature.instance.send(p)
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()

        if (LunarClientAPI.getInstance().isRunningLunarClient(p)) {
            LunarClientAPI.getInstance().disableAllStaffModules(p)
            Chat.sendMessage(p, "${Chat.dash} &7Your &bLunar Client&7 staff modules have been disabled.")
        }
        specChat("&f${p.name}&7 has left spectator mode.", p)
        Chat.sendMessage(p, "${Chat.prefix} You are no longer in spectator mode.")
    }

    fun getSpecs(): List<String> {
        return SettingsFeature.instance.data!!.getStringList("game.specs")
    }

    fun isSpec(p: Player): Boolean {
        return getSpecs().contains(p.name)
    }

    fun specChat(chat: String, p: Player? = null) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (getSpecs().contains(player.name)) {
                if (p != null && p == player) {
                    continue
                }
                Chat.sendMessage(player, "${Chat.prefix} $chat")
            }
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        if (isSpec(p)) {
            e.isCancelled = true
            if (e.currentItem != null && e.currentItem.type != Material.AIR) {
                when (e.currentItem.itemMeta.displayName) {
                    "&cTeleport to 0,0" -> {
                        p.teleport(Location(p.world, 0.0, 100.0, 0.0))
                        Chat.sendMessage(p, "${Chat.prefix} You have been teleported to 0,0.")
                    }
                    "&cNearby Players" -> {
                        Bukkit.dispatchCommand(p, "nearby")
                    }
                    "&cPlayer Locations" -> {
                        val list = ArrayList<Player>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player != p && !isSpec(player)) {
                                list.add(player)
                            }
                        }
                        if (list.isEmpty()) {
                            Chat.sendMessage(p, "${Chat.prefix} There are no players online.")
                        }
                        Chat.sendMessage(p, Chat.line)
                        Chat.sendCenteredMessage(p, "&c&lPlayer Locations")
                        for (player in list) {
                            Chat.sendMessage(p, "${Chat.prefix} &7${player.name} &7is at &b${floor(player.location.x)}, &7${floor(player.location.y)}, &7${floor(player.location.z)}")
                        }
                        Chat.sendMessage(p, Chat.line)
                    }
                    "&cRespawn Players" -> {
                        Bukkit.dispatchCommand(p, "respawn")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (getSpecs().contains(e.player.name)) {
            if (e.player.gameMode == GameMode.SPECTATOR) {
                if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                    e.isCancelled = true
                    val list = ArrayList<Player>()
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (!getSpecs().contains(player.name)) list.add(player)
                    }
                    if (list.isEmpty()) {
                        Chat.sendMessage(e.player, "&cNo players to teleport to!")
                        return
                    }
                    val target = list.random()
                    e.player.teleport(target.location)
                    Chat.sendMessage(e.player, "${Chat.prefix} Teleported to &f${target.name}&7!")
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractWithPlayer(e: PlayerInteractEntityEvent) {
        if (e.player.itemInHand == null) return
        if (getSpecs().contains(e.player.name)) {
            if (e.rightClicked.type == EntityType.PLAYER) {
                val player = (e.rightClicked as Player)
                val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "${player.name}'s Inventory"))
                e.player.openInventory(gui.make())
                InvSeeFeature(e.player, player).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 20L)
            } else {
                Chat.sendMessage(e.player, "&cYou aren't right clicking anyone.")
                return
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (getSpecs().contains(e.player.name)) {
            Schedulers.sync().runLater(runnable@ {
                spec(e.player)
            }, 5L)
        }
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            if (getSpecs().contains(((e.damager) as Player).name)) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onItemPickup(e: PlayerPickupItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    var brokenBlocks: HashMap<UUID, HashSet<Block>> = HashMap<UUID, HashSet<Block>>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        val p = e.player
        if (brokenBlocks.containsKey(p.uniqueId)) {
            if (brokenBlocks[p.uniqueId]!!.contains(e.block)) return
        }
        if (e.block.type == Material.DIAMOND_ORE) {
            var diamonds = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.DIAMOND_ORE) {
                            diamonds++
                            if (diamondsMined[p.uniqueId] == null) diamondsMined[p.uniqueId] = 0
                            diamondsMined[p.uniqueId] = diamondsMined[p.uniqueId]!! + 1
                            if (brokenBlocks.containsKey(p.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[p.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        Chat.sendMessage(
                            player,
                            "$prefix ${PlayerUtils.getPrefix(p)}${p.name}&7 has mined &bDiamond Ore&7. &8(&7T: &b${diamondsMined[p.uniqueId]} &8| &7V: &b${diamonds}&8)"
                        )
                    }, 1L)
                }
            }
        } else if (e.block.type == Material.GOLD_ORE) {
            var gold = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.GOLD_ORE) {
                            gold++
                            if (goldMined[p.uniqueId] == null) goldMined[p.uniqueId] = 0
                            goldMined[p.uniqueId] = goldMined[p.uniqueId]!! + 1
                            if (brokenBlocks.containsKey(p.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[p.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name}&7 has mined &6Gold Ore&7. &8(&7T: &6${goldMined[p.uniqueId]} &8| &7V: &6${gold}&8)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity !is Player) return
        val p = e.entity as Player
        if (e.finalDamage == 0.0 || e.damage == 0.0 || e.isCancelled) return
        if (p.health - e.finalDamage <= 0) return
        val percentage = (e.finalDamage / 2) * 10
        when (e.cause) {
            EntityDamageEvent.DamageCause.FALL -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fFall&7."))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
            EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.MELTING -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fBurning&7."))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }
                }
            }
            EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.PROJECTILE -> {
                return
            }
            else -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fUnknown&7."))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity !is Player) return
        val p = e.entity as Player
        if (e.finalDamage == 0.0 || e.damage == 0.0 || e.isCancelled) return
        if (p.health - e.finalDamage <= 0) return
        val percentage = (e.finalDamage / 2) * 10
        if (e.entity is Monster) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to &fPvE&7."))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        }
        if (e.damager is Player) {
            val damager = e.damager as Player
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to ${PlayerUtils.getPrefix(damager)}${damager.name} &8(${PlayerUtils.getHealth(damager)}&8)&7. &8(&fPvP&8)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        } else if (e.damager is Arrow) {
            val a = e.damager as Arrow
            if (a.shooter is Player) {
                val damager = a.shooter as Player
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} &8(${PlayerUtils.getHealth(p)}&8)&7 took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%&7 due to ${PlayerUtils.getPrefix(damager)}${damager.name} &8(${PlayerUtils.getHealth(damager)}&8)&7. &8(&fBow&8)"))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.SUGGEST_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
        }
    }

}