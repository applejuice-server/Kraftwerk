package pink.mino.kraftwerk.commands

import com.wimbli.WorldBorder.Config
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.pregen")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }

        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} &7Invalid usage: ${ChatColor.RED}/pregen <world> <border> <type> &7or&c /pregen cancel")
            return false
        }
        if (args[0] === "cancel") {
            if (Config.fillTask.valid()) {
                Chat.sendMessage(sender, "${Chat.prefix} Okay, cancelling the pregeneration task.")
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "wb fill cancel"
                )
            } else {
                Chat.sendMessage(sender, "${Chat.prefix} There is no valid pregeneration task running.")
            }
        }
        if (args.size != 3) {
            Chat.sendMessage(sender, "${Chat.prefix} &7Invalid usage: ${ChatColor.RED}/pregen <world> <border> <type> &7or&c /pregen cancel")
            return false
        }
        if (args[2] != "overworld" && args[2] != "cityworld") {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid world type, the valid world types are &ccityworld&7, &cnether&7 and &coverworld&7.")
            return false
        }
        Chat.sendMessage(sender, "${Chat.prefix} &7Please standby, this will take a while.")

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "mvdelete ${args[0]}"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "mvconfirm"
        )

        if (args[2] == "cityworld") {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "mvc ${args[0]} normal -g CityWorld"
            )
        } else if (args[2] == "overworld") {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "mvc ${args[0]} normal"
            )
        } else if (args[2] == "nether") {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "mvc ${args[0]} nether"
            )
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "mvnp link ${settings.data!!.getString("pregen.world")} ${args[0]}"
            )
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${args[0]} clear"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb shape rectangular"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${args[0]} setcorners ${args[1]} ${args[1]} -${args[1]} -${args[1]}"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb ${args[0]} fill 200"
        )
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
            "wb fill confirm"
        )

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), {
            val border = Bukkit.getWorld(args[0]).worldBorder
            border.size = args[1].toDouble() * 2
            border.setCenter(0.0, 0.0)
        }, 5L)

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} &7Pregeneration started in &8'&c${args[0]}&8'&7."))
        PregenActionBarFeature().runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        if (args[0] != "Arena" && args[2] != "nether") {
            settings.data!!.set("pregen.border", args[1].toInt())
            settings.data!!.set("pregen.world", args[0])
            settings.saveData()
        }
        Chat.sendMessage(sender, "${Chat.prefix} View your world using &c/w tp ${args[0]}&7.")

        return true
    }

}