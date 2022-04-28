package pink.mino.kraftwerk.features

import com.lunarclient.bukkitapi.LunarClientAPI
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
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*


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
        p.gameMode = GameMode.CREATIVE

        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list.contains(p.name)) list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()
        updateVisibility()
        specChat("&f${p.name}&7 has entered spectator mode.")
        Chat.sendMessage(p, "${Chat.prefix} You are now in spectator mode.")

        val randomTeleport = ItemStack(Material.SKULL_ITEM, 1, 3)
        val randomTeleportMeta = randomTeleport.itemMeta as SkullMeta
        randomTeleportMeta.owner = p.name
        randomTeleportMeta.displayName = Chat.colored("&cRandom Teleport")
        randomTeleportMeta.lore = listOf(
            Chat.colored("&7Right-click to randomly teleport to a player.")
        )
        randomTeleport.itemMeta = randomTeleportMeta
        p.inventory.setItem(0, randomTeleport)

        val teleportTo00 = ItemStack(Material.GOLDEN_APPLE)
        val teleportTo00Meta = teleportTo00.itemMeta
        teleportTo00Meta.displayName = Chat.colored("&cTeleport to 0,0")
        teleportTo00Meta.lore = listOf(
            Chat.colored("&7Right-click to teleport to &c0,0&7.")
        )
        teleportTo00.itemMeta = teleportTo00Meta
        p.inventory.setItem(8, teleportTo00)

        val invSee = ItemStack(Material.BOOK)
        val invSeeMeta = invSee.itemMeta
        invSeeMeta.displayName = Chat.colored("&cInventory View")
        invSeeMeta.lore = listOf(
            Chat.colored("&7Right-click on a player to view someone's inventory.")
        )
        invSee.itemMeta = invSeeMeta
        p.inventory.setItem(1, invSee)
        val respawnPlayers = ItemBuilder(Material.COMPASS)
            .name("&cRespawn Players")
            .addLore("&7Right-click to respawn players.")
            .make()
        p.inventory.setItem(2, respawnPlayers)
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
        p.gameMode = GameMode.CREATIVE

        SpawnFeature.instance.send(p)
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (!list.contains(p.name)) list.add(p.name)
        SettingsFeature.instance.data!!.set("game.list", list)
        list = SettingsFeature.instance.data!!.getStringList("game.specs")
        list.remove(p.name)
        SettingsFeature.instance.data!!.set("game.specs", list)
        SettingsFeature.instance.saveData()
        updateVisibility()
        if (LunarClientAPI.getInstance().isRunningLunarClient(p)) {
            LunarClientAPI.getInstance().disableAllStaffModules(p)
        }
        specChat("&f${p.name}&7 has left spectator mode.")
        Chat.sendMessage(p, "${Chat.prefix} You are no longer in spectator mode.")
    }

    fun updateVisibility() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (getSpecs().contains(player.name)) {
                for (p2 in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(p2.name)) {
                        player.showPlayer(p2)
                    } else {
                        player.showPlayer(p2)
                    }
                }
            } else {
                for (p2 in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(p2.name)) {
                        player.hidePlayer(p2)
                    } else {
                        player.showPlayer(p2)
                    }
                }
            }
        }
    }

    fun getSpecs(): List<String> {
        return SettingsFeature.instance.data!!.getStringList("game.specs")
    }

    fun specChat(chat: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (getSpecs().contains(player.name)) {
                Chat.sendMessage(player, "${Chat.prefix} $chat")
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (getSpecs().contains(e.player.name)) {
            if (e.item !== null) {
                if (e.item.itemMeta.displayName == Chat.colored("&cRandom Teleport")) {
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
                } else if (e.item.itemMeta.displayName == Chat.colored("&cTeleport to 0,0")) {
                    if (GameState.currentState == GameState.INGAME) {
                        val world = Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world"))
                        val location = Location(world, 0.0, world.getHighestBlockAt(0, 0).location.y + 5.0, 0.0)
                        e.player.teleport(location)
                        Chat.sendMessage(e.player, "${Chat.prefix} Teleported to &c0,0&7.")
                    } else {
                        Chat.sendMessage(e.player, "&cYou can't use this feature yet.")
                    }
                } else if (e.item.itemMeta.displayName == Chat.colored("&cRespawn Players")) {
                    Bukkit.dispatchCommand(e.player, "respawn")
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractWithPlayer(e: PlayerInteractEntityEvent) {
        if (e.player.itemInHand == null) return
        if (getSpecs().contains(e.player.name)) {
            if (e.player.itemInHand.itemMeta.displayName == Chat.colored("&cInventory View")) {
                if (e.rightClicked.type == EntityType.PLAYER) {
                    val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "&cInventory Viewer"))
                    val player = (e.rightClicked as Player)
                    for ((index, item) in player.inventory.contents.withIndex()) {
                        if (item == null) {
                            gui.item(index, ItemStack(Material.AIR)).onClick runnable@ {
                                it.isCancelled = true
                            }
                        } else {
                            gui.item(index, item).onClick runnable@ {
                                it.isCancelled = true
                            }
                        }
                    }
                    if (player.inventory.helmet != null) gui.item(38, player.inventory.helmet).onClick runnable@ {
                        it.isCancelled = true
                    }
                    if (player.inventory.chestplate != null) gui.item(39, player.inventory.chestplate).onClick runnable@ {
                        it.isCancelled = true
                    }
                    if (player.inventory.leggings != null) gui.item(41, player.inventory.leggings).onClick runnable@ {
                        it.isCancelled = true
                    }
                    if (player.inventory.boots != null) gui.item(42, player.inventory.boots).onClick runnable@ {
                        it.isCancelled = true
                    }
                    e.player.openInventory(gui.make())
                } else {
                    Chat.sendMessage(e.player, "&cYou aren't right clicking anyone.")
                    return
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (getSpecs().contains(e.player.name)) {
            if (GameState.currentState == GameState.INGAME || GameState.currentState == GameState.WAITING) {
                spec(e.player)
                Chat.sendMessage(e.player, "${Chat.prefix} You are still currently in spectator mode.")
            } else if (GameState.currentState == GameState.LOBBY) {
                unspec(e.player)
                Chat.sendMessage(e.player, "${Chat.prefix} You've been automatically placed out of spectator mode.")
            }
        }
        updateVisibility()
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
        val percentage = (e.damage / 2) * 10
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
        val percentage = (e.damage / 2) * 10
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