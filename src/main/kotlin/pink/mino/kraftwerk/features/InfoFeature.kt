package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class InfoFeature : BukkitRunnable() {
    val prefix: String = "&8[${Chat.primaryColor}Info&8]&7"
    private val announcements = listOf(
        Chat.colored("$prefix Join our discord server using ${Chat.secondaryColor}/discord&7!"),
        Chat.colored("$prefix Apply for staff using ${Chat.secondaryColor}/apply&7!"),
        Chat.colored("$prefix Want to host games on here? Apply for us! Use ${Chat.secondaryColor}/apply&7!"),
        Chat.colored("$prefix Like our server @ ${Chat.secondaryColor}namemc.com/server/${if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp") else "no server ip setup in config tough tits"}&7!"),
        Chat.colored("$prefix Want more games hosted? Apply for staff @ ${Chat.secondaryColor}/apply&7!"),
        Chat.colored("$prefix Wanna know when games are hosted & more? Join our discord @ ${Chat.secondaryColor}/discord&7."),
       Chat.colored("$prefix View the store using ${Chat.secondaryColor}/store&7!"),
        Chat.colored("$prefix View the server rules using ${Chat.secondaryColor}/rules&7."),
        Chat.colored("$prefix View the health of other players using ${Chat.secondaryColor}/health&7!"),
        Chat.colored("$prefix View the stats of other players using ${Chat.secondaryColor}/stats <player>&7!"),
        Chat.colored("$prefix View the UHC Configuration using ${Chat.secondaryColor}/config&7!"),
        Chat.colored("$prefix Check which scenarios are active using ${Chat.secondaryColor}/scenarios&7!"),
        Chat.colored("$prefix Don't know when the Loot Crate (or other) will spawn? Use ${Chat.secondaryColor}/timers&7!"),
        Chat.colored("$prefix Who has the top kills in the game? Use ${Chat.secondaryColor}/kt&7!"),
        Chat.colored("$prefix Message your team your mined ores using ${Chat.secondaryColor}/pmminedores&7!"),
        Chat.colored("$prefix Message your team the ores you have now using ${Chat.secondaryColor}/pmores&7!"),
        Chat.colored("$prefix Are you a content creator? Apply for media rank using ${Chat.secondaryColor}/media&7!"),
        Chat.colored("$prefix Is someone being annoying to you? Use ${Chat.secondaryColor}/ignore&7!"),
        Chat.colored("$prefix Can't see? Use ${Chat.secondaryColor}/fb&7 to enable night vision!"),
        Chat.colored("$prefix Team with other players using ${Chat.secondaryColor}/team&7!"),
        Chat.colored("$prefix Announce where you are to fight with other players using ${Chat.secondaryColor}/fight&7!"),
        Chat.colored("$prefix Thank the host using ${Chat.secondaryColor}/thanks&7!")
    )
    override fun run() {
        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            if (!ConfigOptionHandler.getOption("private")!!.enabled) {
                Bukkit.broadcastMessage(announcements[Random.nextInt(announcements.size)])
            }
        }
    }
}