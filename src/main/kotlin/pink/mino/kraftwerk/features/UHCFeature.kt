package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.commands.SendTeamView
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.StatsHandler
import kotlin.math.floor

// https://github.com/LeonTG/Timer/blob/main/src/main/java/com/leontg77/timer/runnable/TimerRunnable.java#L157
private fun timeToString(ticks: Long): String {
    var t = ticks
    val hours = floor(t / 3600.toDouble()).toInt()
    t -= hours * 3600
    val minutes = floor(t / 60.toDouble()).toInt()
    t -= minutes * 60
    val seconds = t.toInt()
    val output = StringBuilder()
    if (hours > 0) {
        output.append(hours).append('h')
        if (minutes == 0) {
            output.append(minutes).append('m')
        }
    }
    if (minutes > 0) {
        output.append(minutes).append('m')
    }
    output.append(seconds).append('s')
    return output.toString()
}

enum class Events {
    PRE_START,
    START,
    FINAL_HEAL,
    PVP,
    MEETUP
}

class UHCTask : BukkitRunnable() {
    val finalHeal = SettingsFeature.instance.data!!.getInt("game.events.final-heal") * 60 + 45
    val pvp = ((SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal")) * 60) + 45
    val meetup = ((SettingsFeature.instance.data!!.getInt("game.events.pvp") + SettingsFeature.instance.data!!.getInt("game.events.final-heal") + SettingsFeature.instance.data!!.getInt("game.events.meetup")) * 60) + 45

    val rawPvP = (SettingsFeature.instance.data!!.getInt("game.events.pvp") * 60) + 45
    val rawMeetup = (SettingsFeature.instance.data!!.getInt("game.events.meetup") * 60) + 45

    var timer = 0
    var currentEvent: Events = Events.PRE_START

    private fun displayTimer(player: Player) {
        when (currentEvent) {
            Events.PRE_START -> {
                ActionBar.sendActionBarMessage(player, "&cStarting in ${Chat.dash} &f${timeToString((45 - timer).toLong())}")
            }
            Events.START -> {
                ActionBar.sendActionBarMessage(player, "&cFinal Heal is in ${Chat.dash} &f${timeToString((finalHeal - timer).toLong())}")
            }
            Events.FINAL_HEAL -> {
                ActionBar.sendActionBarMessage(player, "&cPvP is enabled in ${Chat.dash} &f${timeToString((pvp - timer).toLong())}")
            }
            Events.PVP -> {
                ActionBar.sendActionBarMessage(player, "&cMeetup is in ${Chat.dash} &f${timeToString((meetup - timer).toLong())}")
            }
            Events.MEETUP -> {
                ActionBar.sendActionBarMessage(player, "&cIt is now Meetup! Head to 0,0! &8| &7Border: &f±${SettingsFeature.instance.data!!.getInt("pregen.border")}")
            }
        }
    }

    override fun run() {
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        when (timer) {
            0 -> {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Starting in &f45 seconds&7..."))
            }
            45 -> {
                currentEvent = Events.START
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cc")
                GameState.setState(GameState.INGAME)
                UHCFeature().unfreeze()
                val scenarios = ArrayList<String>()
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenarios.add(scenario.name)
                }
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                }
                Bukkit.broadcastMessage(" ")
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendMessage(player, "&7You may &abegin&7! The host for this game is &c${SettingsFeature.instance.data!!.getString("game.host")}&7!")

                    Chat.sendMessage(player, "&7Scenarios: &f${scenarios.joinToString(", ")}&7")
                    Chat.sendCenteredMessage(player, " ")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 10F, 1F)
                    player.sendTitle(Chat.colored("&a&lGO!"), Chat.colored("&7You may now play the game, do &c/helpop&7 for help!"))
                    if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                        player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, SettingsFeature.instance.data!!.getInt("game.starterfood")))
                        list.add(player.name)
                        if (!ConfigOptionHandler.getOption("statless")!!.enabled) StatsHandler.getStatsPlayer(player).add("games_played", 1)
                    }
                }
                SettingsFeature.instance.data!!.set("game.list", list)
                SettingsFeature.instance.saveData()
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onStart()
                }
                Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", true.toString())
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer cancel")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer ${SettingsFeature.instance.data!!.getInt("game.events.final-heal") * 60} &cFinal Heal is in ${Chat.dash}&f")
                for (player in Bukkit.getOnlinePlayers()) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 100, true, true))
                }
            }
            finalHeal -> {
                currentEvent = Events.FINAL_HEAL
                for (player in Bukkit.getOnlinePlayers()) {
                    player.health = player.maxHealth
                    player.foodLevel = 20
                    player.saturation = 20F
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7All players have been healed & fed.")
                    Chat.sendCenteredMessage(player, "&cPvP&7 is enabled in &c${rawPvP / 60} minutes&7.")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.BURP, 10F, 1F)
                }
            }
            pvp -> {
                currentEvent = Events.PVP
                for (world in Bukkit.getWorlds()) {
                    world.pvp = true
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onPvP()
                }
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendMessage(player, Chat.line)
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7PvP has been &aenabled&7.")
                    Chat.sendCenteredMessage(player, "&cMeetup&7 is enabled in &c${rawMeetup / 60} minutes&7.")
                    Chat.sendMessage(player, Chat.line)
                    player.playSound(player.location, Sound.ANVIL_LAND, 10F, 1F)
                }
            }
            meetup -> {
                currentEvent = Events.MEETUP
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                for (player in Bukkit.getOnlinePlayers()) {
                    Chat.sendCenteredMessage(player, "&c&lUHC")
                    Chat.sendMessage(player, " ")
                    Chat.sendCenteredMessage(player, "&7It's now &cMeetup&7! Head to &a0,0&7!")
                    Chat.sendCenteredMessage(player, "&7The border will start shrinking until it's at &f25x25&7!")
                }
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.onMeetup()
                }
                Bukkit.broadcastMessage(Chat.colored(Chat.line))
                UHCFeature().scheduleShrink(500)
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    UHCFeature().scheduleShrink(250)
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        UHCFeature().scheduleShrink(100)
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            UHCFeature().scheduleShrink(75)
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                UHCFeature().scheduleShrink(50)
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                    UHCFeature().scheduleShrink(25)
                                }, 6000)
                            }, 6000)
                        }, 6000)
                    }, 6000)
                }, 6000)
            }
        }
        for (player in Bukkit.getOnlinePlayers()) {
            displayTimer(player)
        }
        timer++
    }
}

class UHCFeature : Listener {
    fun start(mode: String) {
        GameState.setState(GameState.WAITING)
        Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).time = 1000
        Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")).setGameRuleValue("doDaylightCycle", false.toString())
        for (world in Bukkit.getWorlds()) {
            world.pvp = false
        }
        if (mode == "ffa") {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Starting a &cFFA&7 UHC game... now freezing players."))
        } else if (mode == "teams") {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Starting a &cTeams&7 UHC game... now freezing players."))
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    if (TeamsFeature.manager.getTeam(player) == null) {
                        for (team in TeamsFeature.manager.getTeams()) {
                            if (team.size == 0) {
                                team.addPlayer(player)
                                SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                            }
                        }
                    }
                }
            }
        }
        var list = SettingsFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                SpawnFeature.instance.send(player)
                CombatLogFeature.instance.removeCombatLog(player.name)
                player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
                player.enderChest.clear()
                player.maxHealth = 20.0
                player.health = 20.0
                player.foodLevel = 20
                player.saturation = 20F
                player.exp = 0F
                player.level = 0
                player.gameMode = GameMode.SURVIVAL
                player.inventory.clear()
                player.inventory.armorContents = null
                player.itemOnCursor = ItemStack(Material.AIR)
                val openInventory = player.openInventory
                if (openInventory.type == InventoryType.CRAFTING) {
                    openInventory.topInventory.clear()
                }
                val effects = player.activePotionEffects
                for (effect in effects) {
                    player.removePotionEffect(effect.type)
                }
            }
        }
        SettingsFeature.instance.data!!.set("game.list", list)
        SettingsFeature.instance.saveData()
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl all")
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl on")
        if (mode == "ffa") {
            ScatterFeature.scatter("ffa", Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")), SettingsFeature.instance.data!!.getInt("pregen.border"), true)
        } else if (mode == "teams") {
            ScatterFeature.scatter("teams", Bukkit.getWorld(SettingsFeature.instance.data!!.getString("pregen.world")), SettingsFeature.instance.data!!.getInt("pregen.border"), true)
        }
        freeze()
        JavaPlugin.getPlugin(Kraftwerk::class.java).game = UHCTask()
        JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
    }

    fun freeze() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 999999999, 10, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 999999999, 100, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999999, -100, true, false))
                player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999999, 1000, true, false))
            }
        }
    }

    fun unfreeze() {
        for (player in Bukkit.getOnlinePlayers()) {
            player.removePotionEffect(PotionEffectType.SLOW)
            player.removePotionEffect(PotionEffectType.JUMP)
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
            player.removePotionEffect(PotionEffectType.BLINDNESS)
        }
    }

    fun scheduleShrink(newBorder: Int) {
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f10s&7."))
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f9s&7."))
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f8s&7."))
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f7s&7."))
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f6s&7."))
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f5s&7."))
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f4s&7."))
                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f3s&7."))
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f2s&7."))
                                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Shrinking to &f${newBorder}x${newBorder}&7 in &f1s&7."))
                                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "border $newBorder")
                                                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                                                    Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The border has shrunken to &f${newBorder}x${newBorder}&7."))
                                                    if (newBorder != 25) {
                                                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Next border shrink in &f5 minutes."))
                                                    }
                                                    Bukkit.broadcastMessage(Chat.colored(Chat.line))
                                                }, 20L)
                                            }, 20L)
                                        }, 20L)
                                    }, 20L)
                                }, 20L)
                            }, 20L)
                        }, 20L)
                    }, 20L)
                }, 20L)
            }, 20L)
        }, 20L)
    }
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
        /*
        if (GameState.currentState == GameState.INGAME) {
            when (e.block.type) {
                Material.DIAMOND_ORE -> {
                    Stats.addDiamondMined(e.player)
                }
                Material.GOLD_ORE -> {
                    Stats.addGoldMined(e.player)
                }
                Material.IRON_ORE -> {
                    Stats.addIronMined(e.player)
                }
            }
        }*/
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (GameState.currentState == GameState.WAITING) {
            e.isCancelled = true
        }
    }
}