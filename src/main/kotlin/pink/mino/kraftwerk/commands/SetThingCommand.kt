package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

class SetThingCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.admin.setthing")) {
                Chat.sendMessage(sender, "&cOnly admins can execute this command.")
                return false
            }
        }
        if (sender !is Player) {
            sender.sendMessage("Can't use this command bucko.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "&cInvalid arguments.")
            Chat.sendMessage(sender, "${Chat.dash} &7Valid arguments are: ${Chat.secondaryColor}kit_editor, games_played_holo, wins_holo, kills_holo, diamonds_mined_holo, gold_mined_holo, gapples_eaten_holo, highest_level_holo, latest_match_holo&7.")
            return true
        }
        if (args[0].lowercase() == "kit_editor"){
            SettingsFeature.instance.data!!.set("config.thing.kit_editor.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.kit_editor.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.kit_editor.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.kit_editor.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The kit editor has been set to your location.")
        }
        if (args[0].lowercase() == "games_played_holo"){
            SettingsFeature.instance.data!!.set("config.thing.games_played_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.games_played_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.games_played_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.games_played_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The games played hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "wins_holo"){
            SettingsFeature.instance.data!!.set("config.thing.wins_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.wins_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.wins_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.wins_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The wins hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "kills_holo"){
            SettingsFeature.instance.data!!.set("config.thing.kills_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.kills_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.kills_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.kills_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The kills hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "diamonds_mined_holo"){
            SettingsFeature.instance.data!!.set("config.thing.diamonds_mined_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.diamonds_mined_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.diamonds_mined_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.diamonds_mined_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The diamonds mined hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "gold_mined_holo"){
            SettingsFeature.instance.data!!.set("config.thing.gold_mined_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.gold_mined_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.gold_mined_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.gold_mined_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The gold mined hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "gapples_eaten_holo"){
            SettingsFeature.instance.data!!.set("config.thing.gapples_eaten_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.gapples_eaten_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.gapples_eaten_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.gapples_eaten_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The gapples eaten hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "highest_level_holo"){
            SettingsFeature.instance.data!!.set("config.thing.highest_level_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.highest_level_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.highest_level_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.highest_level_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The highest level hologram has been set to your location. (note: this change will apply next restart)")
        }
        if (args[0].lowercase() == "latest_match_holo"){
            SettingsFeature.instance.data!!.set("config.thing.latest_match_holo.x", sender.location.x)
            SettingsFeature.instance.data!!.set("config.thing.latest_match_holo.y", sender.location.y)
            SettingsFeature.instance.data!!.set("config.thing.latest_match_holo.z", sender.location.z)
            SettingsFeature.instance.data!!.set("config.thing.latest_match_holo.world", sender.location.world.name)
            SettingsFeature.instance.saveData()
            Chat.sendMessage(sender, "${Chat.prefix} The latest match hologram has been set to your location. (note: this change will apply next restart)")
        }
        return true
    }
}