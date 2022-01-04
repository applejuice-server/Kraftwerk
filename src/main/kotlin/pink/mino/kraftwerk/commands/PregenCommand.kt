package pink.mino.kraftwerk.commands

import com.wimbli.WorldBorder.Config
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.Settings
import kotlin.math.floor


class PregenCommand : CommandExecutor {

    private val settings: Settings = Settings.instance

    override fun onCommand(sender: CommandSender, command: Command, label: String?, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.command.pregen")) {
            sender.sendMessage("${ChatColor.RED}You don't have permission to use this command.")
            return false
        }

        if (args.isEmpty() || args[1].isEmpty()) {
            Chat.sendMessage(sender as Player, "&7Invalid usage: ${ChatColor.GREEN}/pregen <world> <border>")
            return false
        }


        val wc = WorldCreator(args[0])
        wc.environment(World.Environment.NORMAL)
        wc.type(WorldType.NORMAL)
        wc.generateStructures(true)
        val world = Bukkit.createWorld(wc)

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${world.name} clear"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb shape rectangular"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${world.name} setcorners ${args[1]} ${args[1]} -${args[1]} -${args[1]}"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${world.name} fill 75"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb fill confirm"
        )

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7Pregeneration started in &f${args[0]}&7."))

        runActionBar()
        settings.data!!.set("pregen.border", args[1])
        settings.data!!.set("pregen.world", args[0])
        settings.saveData()

        return true
    }

    private fun runActionBar() {
        Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Kraftwerk::class.java) /* ? */, Runnable breakout@{
            if (Config.fillTask.valid()) {
                val players = Bukkit.getServer().onlinePlayers
                for (player in players) {
                    ActionBar.sendActionBarMessage(player, "&7Progress: &c${floor(Config.fillTask.percentageCompleted)}% &8| &7World: &c${Config.fillTask.refWorld()}")
                }
            } else {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7Pregeneration is now finished."))
                Bukkit.getServer().scheduler.cancelTask(1)
            }
        }, 0, 1)
    }


}