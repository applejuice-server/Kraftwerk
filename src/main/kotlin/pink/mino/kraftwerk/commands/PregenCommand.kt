package pink.mino.kraftwerk.commands

import com.wimbli.WorldBorder.Config
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.PregenActionBarFeature
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat


class PregenCommand : CommandExecutor {

    private val settings: SettingsFeature = SettingsFeature.instance

    override fun onCommand(sender: CommandSender, command: Command, label: String?, args: Array<String>): Boolean {
        if (!sender.hasPermission("uhc.command.pregen")) {
            Chat.sendMessage(sender as Player, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
            return false
        }

        if (args.isEmpty() || args[1].isEmpty()) {
            Chat.sendMessage(sender as Player, "${Chat.prefix} &7Invalid usage: ${ChatColor.RED}/pregen <world> <border> &8or&c /pregen cancel")
            return false
        }
        if (args[0] === "cancel") {
            if (Config.fillTask.valid()) {
                Chat.sendMessage(sender as Player, "${Chat.prefix} Okay, cancelling the pregeneration task.")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "wb fill cancel"
                )
            } else {
                Chat.sendMessage(sender as Player, "${Chat.prefix} There is no valid pregeneration task running.")
            }
        }
        Chat.sendMessage(sender as Player, "${Chat.prefix} &7Please standby, this will take a while.")

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

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} &7Pregeneration started in &8'&c${args[0]}&8'&7."))
        PregenActionBarFeature().runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        settings.data!!.set("pregen.border", args[1].toInt())
        settings.data!!.set("pregen.world", args[0])
        settings.saveData()

        return true
    }

}