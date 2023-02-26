package pink.mino.kraftwerk.listeners

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
import pink.mino.kraftwerk.utils.PlayerUtils

class ChatListener : Listener {

    private var vaultChat: Chat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(Chat::class.java)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val group: String? = vaultChat?.getPrimaryGroup(player)
        val prefix: String = ChatColor.translateAlternateColorCodes('&', vaultChat?.getGroupPrefix(player.world, group))
        if (player.hasPermission("uhc.donator.emotes")) {
            if (e.message.contains(":shrug:", true)) {
                e.message = e.message.replace(":shrug:", pink.mino.kraftwerk.utils.Chat.colored("&e¯\\_(ツ)_/¯&r"))
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
                Bukkit.dispatchCommand(player, "mcc ${e.message}")
            }
        }
        if (preference == "STAFF") {
            e.isCancelled = true
            if (!player.hasPermission("uhc.staff")) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Bukkit.dispatchCommand(player, "ac ${e.message}")
            }
        }
        if (preference == "SPEC") {
            e.isCancelled = true
            if (!SpecFeature.instance.isSpec(player)) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Bukkit.dispatchCommand(player, "sc ${e.message}")
            }
        }
        if (preference == "TEAM") {
            e.isCancelled = true
            if (TeamsFeature.manager.getTeam(player) == null) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "&cYou aren't on a Team. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Bukkit.dispatchCommand(player, "pm ${e.message}")
            }
        }
        if (preference == "PUBLIC") {
            e.isCancelled = false
            if (player.hasPermission("uhc.donator.whitechat")) {
                e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE + "%s"
            } else {
                e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%s"
            }
        }
    }

}