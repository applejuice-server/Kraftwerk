package pink.mino.kraftwerk.features

import com.lunarclient.bukkitapi.LunarClientAPI
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.GuiBuilder

class SpecFeature : Listener {
    companion object {
        val instance = SpecFeature()
    }

    fun checkPlayer(p: OfflinePlayer?) {
        var statement = "SELECT (uuid) from spectate WHERE uuid = '${p!!.uniqueId}'"
        val result = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection.createStatement().executeQuery(statement)
        if (!result.isBeforeFirst) {
            statement = "INSERT INTO spectate (uuid) VALUES ('${p.uniqueId}')"
            with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.connection) {
                createStatement().execute(statement)
            }
        }
    }

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

}