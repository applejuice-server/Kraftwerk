package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class InfoFeature : BukkitRunnable() {
    val prefix: String = "&8[&4Info&8]&7"
    private val announcements = listOf(
        Chat.colored("$prefix Join our discord server using &f/discord&7!"),
        Chat.colored("$prefix Apply for staff using &f/apply&7!"),
        Chat.colored("$prefix Like our server @ &fnamemc.com/server/${SettingsFeature.instance.data!!.getString("server.region").lowercase()}.applejuice.bar&7!"),
        Chat.colored("$prefix Want more games hosted? Apply for staff @ &f/apply&7!"),
        Chat.colored("$prefix Wanna know when games are hosted & more? Join our discord @ &f/discord&7."),
        Chat.colored("$prefix Follow us on twitter &b@applejuiceuhc&7!"),
        Chat.colored("$prefix View the store using &f/store&7!"),
        Chat.colored("$prefix View the server rules using &f/rules&7."),
        Chat.colored("$prefix View the health of other players using &f/health&7!"),
        Chat.colored("$prefix View the stats of other players using &f/stats <player>&7!"),
        Chat.colored("$prefix View the UHC Configuration using &f/config&7!"),
        Chat.colored("$prefix Check which scenarios are active using &f/scenarios&7!")
    )
    override fun run() {
        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            Bukkit.broadcastMessage(announcements[Random.nextInt(announcements.size)])
        }
    }
}