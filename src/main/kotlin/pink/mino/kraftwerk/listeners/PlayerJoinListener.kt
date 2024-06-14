package pink.mino.kraftwerk.listeners

import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.commands.WhitelistCommand
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.*
import net.milkbowl.vault.chat.Chat as VaultChat

class PlayerJoinListener : Listener {

    private var vaultChat: VaultChat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(net.milkbowl.vault.chat.Chat::class.java)
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        Scoreboard.setScore(Chat.colored("${Chat.dash} &7Playing..."), PlayerUtils.getPlayingPlayers().size)

        for (online in Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.showPlayer(player)
                player.showPlayer(online)
            }
        }

        val group: String = vaultChat!!.getPrimaryGroup(player)
        val prefix: String = if (vaultChat!!.getGroupPrefix(player.world, group) != "&7") Chat.colored(vaultChat!!.getGroupPrefix(player.world, group)) else Chat.colored("&a")
        e.joinMessage = ChatColor.translateAlternateColorCodes('&', "&8(&2+&8)&r ${prefix}${player.displayName} &8[&2${Bukkit.getOnlinePlayers().size}&8/&2${Bukkit.getServer().maxPlayers}&8]")
        /*Schedulers.sync().runLater({
            Chat.sendMessage(player, "&8➡ &7Please consider donating to the server to keep it up for another month! The store link is &ehttps://applejuice.tebex.io&7 or just use &c/buy&7!")
        }, 1L)*/
        if (GameState.currentState == GameState.LOBBY) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                SpawnFeature.instance.send(player)
                if (PerkChecker.checkPerks(player).contains(Perk.SPAWN_FLY)) {
                    player.allowFlight = true
                    player.isFlying = true
                }
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.spec(player)
                }
            }, 1L)
        } else {
            val scatter = JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs
            if (scatter.contains(player.name.lowercase())) {
                if (JavaPlugin.getPlugin(Kraftwerk::class.java).scattering) {
                    return
                }
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.joinSpec(player)
                    return
                }

                if (GameState.currentState == GameState.WAITING || UHCFeature().scattering) {
                    UHCFeature().freeze()
                }
                player.teleport(scatter[player.name.lowercase()])
                scatter.remove(player.name.lowercase())
                if (TeamsFeature.manager.getTeam(player) != null) {
                    for (entry in TeamsFeature.manager.getTeam(player)!!.entries) {
                        val tm = Bukkit.getPlayer(entry) ?: continue
                        tm.hidePlayer(player)
                        object: BukkitRunnable() {
                            override fun run() {
                                tm.showPlayer(player)
                            }
                        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 3L)
                    }
                }

                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Automatically late scattered ${Chat.primaryColor}${player.name}&7."))
                player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
                player.maxHealth = 20.0
                player.health = player.maxHealth
                player.isFlying = false
                player.allowFlight = false
                player.foodLevel = 20
                player.saturation = 20F
                player.gameMode = GameMode.SURVIVAL
                player.inventory.clear()
                player.inventory.armorContents = null
                player.enderChest.clear()
                player.itemOnCursor = ItemStack(Material.AIR)
                val openInventory = player.openInventory
                if (openInventory.type == InventoryType.CRAFTING) {
                    openInventory.topInventory.clear()
                }
                val effects = player.activePotionEffects
                for (effect in effects) {
                    player.removePotionEffect(effect.type)
                }
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 1000, true, false))
                player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, SettingsFeature.instance.data!!.getInt("game.starterfood")))
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.gamesPlayed++
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.givePlayer(player)
                }
                WhitelistCommand().addWhitelist(player.name.lowercase())
                var list = SettingsFeature.instance.data!!.getStringList("game.list")
                if (list == null) list = ArrayList<String>()
                list.add(player.name)
                SettingsFeature.instance.data!!.set("game.list", list)
                SettingsFeature.instance.saveData()
                return
            }
            if (GameState.currentState == GameState.WAITING) {
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.joinSpec(player)
                    return
                }
                return
            } else {
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.joinSpec(player)
                    return
                }
                if (!SettingsFeature.instance.data!!.getStringList("game.list").contains(player.name)) {
                    if (SpecFeature.instance.getSpecs().contains(player.name)) {
                        return
                    }
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        SpawnFeature.instance.send(player)
                    }, 1L)
                    SpecFeature.instance.specChat("${Chat.secondaryColor}${player.name}&7 hasn't been late-scattered, sending them to spawn.")
                    val comp = TextComponent(Chat.colored("${Chat.dash} &d&lLatescatter player?"))
                    val comp2 = TextComponent(Chat.colored("${Chat.dash} ${Chat.primaryColor}&lInsert latescatter command?"))
                    comp.clickEvent = ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/ls ${player.name}"
                    )
                    comp2.clickEvent = ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/ls ${player.name} "
                    )
                    SpecFeature.instance.getSpecs().forEach {
                        val p = Bukkit.getOfflinePlayer(it)
                        if (p.isOnline) {
                            (p as Player).spigot().sendMessage(comp)
                            p.spigot().sendMessage(comp2)
                        }
                    }
                }
            }
        }
        if (GameState.currentState != GameState.WAITING && player.hasPotionEffect(PotionEffectType.JUMP) &&
            player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
            player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE) &&
            player.hasPotionEffect(PotionEffectType.SLOW_DIGGING) &&
            player.hasPotionEffect(PotionEffectType.SLOW) &&
            player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            player.removePotionEffect(PotionEffectType.JUMP)
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING)
            player.removePotionEffect(PotionEffectType.SLOW)
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            if (JavaPlugin.getPlugin(Kraftwerk::class.java).fullbright.contains(e.player.name.lowercase())) {
                player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 1028391820, 0, false, false))
            }
        }, 5L)
    }
}