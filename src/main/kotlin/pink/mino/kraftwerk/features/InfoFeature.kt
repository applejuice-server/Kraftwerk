package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class InfoFeature : BukkitRunnable() {
    val prefix: String = "&8[&4Info&8]&7"
    private val announcements = listOf(
        Chat.colored("$prefix Join our discord server using &c/discord&7!"),
        Chat.colored("$prefix Apply for staff using &c/apply&7!"),
        Chat.colored("$prefix Like our server @ &cnamemc.com/server/applejuice.bar&7!"),
        Chat.colored("$prefix This server was custom coded by &fminota&7."),
        Chat.colored("$prefix Want more games hosted? Apply for staff @ &c/apply&7!"),
        Chat.colored("$prefix Wanna know when games are hosted & more? Join our discord @ &c/discord&7."),
        Chat.colored("$prefix Follow us on twitter &b@applejuiceuhc&7!"),
        Chat.colored("$prefix View the store using &c/store&7!"),
        Chat.colored("$prefix View the server rules using &c/rules&7.")
    )
    override fun run() {
        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            Bukkit.broadcastMessage(announcements[Random.nextInt(announcements.size)])
        }
    }
}