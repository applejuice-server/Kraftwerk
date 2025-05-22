package pink.mino.kraftwerk.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.Events
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class PlayerConnectListener : Listener {
    @EventHandler
    fun onPlayerConnect(e: PlayerLoginEvent) {
        val player = e.player
        if (ConfigFeature.instance.data!!.getBoolean("whitelist.enabled")) {
            if (!ConfigFeature.instance.data!!.getList("whitelist.list").contains(player.name.lowercase())) {
                if (!player.hasPermission("uhc.staff")) {
                    if (GameState.currentState == GameState.INGAME) {
                        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
                            e.disallow(
                                PlayerLoginEvent.Result.KICK_WHITELIST,
                                Chat.colored("&cYou are not allowed to join while the whitelist is on!\n&cThere is currently no game happening on this server.\n\n&7Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}&7!")
                            )
                            return
                        }
                        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent == Events.PVP || JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent == Events.MEETUP) {
                            e.disallow(
                                PlayerLoginEvent.Result.KICK_WHITELIST,
                                Chat.colored("&cYou are not allowed to join while the whitelist is on!\n&cPvP is currently enabled and no more players will be able to join late!\n\n&7Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}&7!")
                            )
                        } else {
                            if (ConfigFeature.instance.data!!.getString("matchpost.team") != null || ConfigFeature.instance.data!!.getString("matchpost.team") == "Auctions" || ConfigFeature.instance.data!!.getString("matchpost.team").contains("Random")) {
                                e.disallow(
                                    PlayerLoginEvent.Result.KICK_WHITELIST,
                                    Chat.colored("&cYou are not allowed to join while the whitelist is on!\n&cYou cannot late scatter in an Auction or a Random teams game!\n\n&7Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}&7!")
                                )
                                return
                            }
                            e.disallow(
                                PlayerLoginEvent.Result.KICK_WHITELIST,
                                Chat.colored("&cYou are not allowed to join while the whitelist is on!\n&cPlease wait until the host accepts late scatters!\n\n&7Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}&7!")
                            )
                        }
                    } else {
                        e.disallow(
                            PlayerLoginEvent.Result.KICK_WHITELIST,
                            Chat.colored("&cYou are not allowed to join while the whitelist is on!\n&cThere is currently no game happening on this server.\n\n&7Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}&7!")
                        )
                    }
                } else {
                    return
                }
            } else {
                return
            }
        } else {
            return
        }
    }
}