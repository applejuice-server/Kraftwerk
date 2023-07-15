package pink.mino.kraftwerk.listeners

import me.lucko.helper.Schedulers
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker
import pink.mino.kraftwerk.utils.PlayerUtils
import java.util.*

class ChatListener : Listener {

    private var vaultChat: Chat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(Chat::class.java)
    }

    val slurs = arrayListOf(
        "tranny",
        "nigger",
        "faggot",
        "retard",
        "kys",
        "nigga",
        "negro",
        "sreggin"
    )

    val cooldowns = hashMapOf<UUID, Long>()
    val cooldownTime: Int = 3

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val group: String? = vaultChat?.getPrimaryGroup(player)
        val prefix: String = ChatColor.translateAlternateColorCodes('&', vaultChat?.getGroupPrefix(player.world, group))
        if (PerkChecker.checkPerks(player).contains(Perk.EMOTES)) {
            if (e.message.contains(":shrug:", true)) {
                e.message = e.message.replace(":shrug:", pink.mino.kraftwerk.utils.Chat.colored("&e¯\\_(ツ)_/¯&r"))
            }
            if (e.message.contains(":yes:", true)) {
                e.message = e.message.replace(":yes:", pink.mino.kraftwerk.utils.Chat.colored("&l&a✔&r"))
            }
            if (e.message.contains(":no:", true)) {
                e.message = e.message.replace(":no:", pink.mino.kraftwerk.utils.Chat.colored("&l&c✖&r"))
            }
            if (e.message.contains("123", true)) {
                e.message = e.message.replace("123", pink.mino.kraftwerk.utils.Chat.colored("&a1&e2&c3&r"))
            }
            if (e.message.contains("<3", true)) {
                e.message = e.message.replace("<3", pink.mino.kraftwerk.utils.Chat.colored("&c❤&r"))
            }
            if (e.message.contains("o/", true)) {
                e.message = e.message.replace("o/", pink.mino.kraftwerk.utils.Chat.colored("&d(・∀・)ノ&r"))
            }
            if (e.message.contains(":star:", true)) {
                e.message = e.message.replace(":star:", pink.mino.kraftwerk.utils.Chat.colored("&e✰&r"))
            }
            if (e.message.contains(":100:", true)) {
                e.message = e.message.replace(":100:", pink.mino.kraftwerk.utils.Chat.colored("&c&o&l&n100&r"))
            }
            if (e.message.contains("o7", true)) {
                e.message = e.message.replace("o7", pink.mino.kraftwerk.utils.Chat.colored("&e(｀-´)>&r"))
            }
            if (e.message.contains(":blush:", true)) {
                e.message = e.message.replace(":blush:", pink.mino.kraftwerk.utils.Chat.colored("&d(◡‿◡✿)&r"))
            }
        }
        var preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode
        if (preference == "MOLES") {
            e.isCancelled = true
            if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles")) || MolesScenario.instance.moles[player.uniqueId] == null) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cMoles is not enabled. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "mcc ${e.message}")
                }
            }
        }
        if (preference == "STAFF") {
            e.isCancelled = true
            if (!player.hasPermission("uhc.staff")) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "ac ${e.message}")
                }
            }
        }
        if (preference == "SPEC") {
            e.isCancelled = true
            if (!SpecFeature.instance.isSpec(player)) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "sc ${e.message}")
                }
            }
        }
        if (preference == "TEAM") {
            e.isCancelled = true
            if (TeamsFeature.manager.getTeam(player) == null) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou aren't on a Team. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "pm ${e.message}")
                }
            }
        }
        if (preference == "PUBLIC") {
            if (!PerkChecker.checkPerks(e.player).contains(Perk.NO_CHAT_DELAY)) {
                val secondsLeft: Long = cooldowns[e.player.uniqueId]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                if (secondsLeft > 0) {
                    pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou are currently on cooldown for ${secondsLeft}s. Skip the cooldown by purchasing a rank at &ehttps://applejuice.tebex.io&c.")
                }
                return
            }
            e.isCancelled = false
            if (!PerkChecker.checkPerks(e.player).contains(Perk.WHITE_CHAT)) {
                val words = e.message.split(" ")
                for (word in words) {
                    if (slurs.contains(word.lowercase())) {
                        e.isCancelled = true
                        pink.mino.kraftwerk.utils.Chat.sendMessage(e.player, "$prefix ${PlayerUtils.getPrefix(player)}${player.name} &8»&7 ${e.message}")
                        Schedulers.sync().runLater({
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute ${player.name} -s 1d Inappropiate Language (auto)")
                        }, (5 * 20).toLong())
                    }
                }
            }
            if (PerkChecker.checkPerks(player).contains(Perk.WHITE_CHAT)) {
                e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + "%s"
            } else {
                e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%s"
            }
            cooldowns[player.uniqueId] = System.currentTimeMillis()
        }
    }

}