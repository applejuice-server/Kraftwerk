package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.features.SpecFeature
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

        val group: String = vaultChat!!.getPrimaryGroup(player)
        val prefix: String = if (vaultChat!!.getGroupPrefix(player.world, group) != "&7") Chat.colored(vaultChat!!.getGroupPrefix(player.world, group)) else Chat.colored("&a")
        e.joinMessage = ChatColor.translateAlternateColorCodes('&', "&8(&2+&8) ${prefix}${player.displayName} &8[&2${Bukkit.getOnlinePlayers().size}&8/&2${Bukkit.getServer().maxPlayers}&8]")
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
            if (SpecFeature.instance.getSpecs().contains(player.name)) {
                SpecFeature.instance.joinSpec(player)
                return
            }
            if (!SettingsFeature.instance.data!!.getStringList("game.list").contains(player.name)) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
                    SpawnFeature.instance.send(player)
                }, 1L)
                SpecFeature.instance.specChat("&f${player.name}&7 hasn't been late-scattered, sending them to spawn.")
            }
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            if (JavaPlugin.getPlugin(Kraftwerk::class.java).fullbright.contains(e.player.name.lowercase())) {
                player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 1028391820, 0, false, false))
            }
        }, 5L)
    }
}