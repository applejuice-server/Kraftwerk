package pink.mino.kraftwerk.scenarios

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import pink.mino.kraftwerk.features.SettingsFeature
import pink.mino.kraftwerk.utils.Chat

abstract class Scenario(
    var name: String,
    var description: String,
    var id: String,
    var material: Material,
    var enabled: Boolean = false,
    var command: Boolean = false,
    var commandName: String = "none",
    var executor: CommandExecutor? = null
): Listener {
    init {
        if (SettingsFeature.instance.data!!.getString("game.scenarios.$id").isNullOrEmpty()) {
            enabled = false
            SettingsFeature.instance.data!!.set("game.scenarios.$id", enabled)
            SettingsFeature.instance.saveData()
        }
        enabled = SettingsFeature.instance.data!!.getBoolean("game.scenarios.$id")
    }

    fun toggle() {
        enabled = !enabled
        val changerText: String = if (enabled) {
            "&aenabled"
        } else {
            "&cdisabled"
        }
        onToggle(enabled)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &e$name&7 has been $changerText&7."))
        SettingsFeature.instance.data!!.set("game.scenarios.$id", enabled)
        SettingsFeature.instance.saveData()
    }

    @JvmName("setEnabled1")
    fun setEnabled(to: Boolean) {
        enabled = to
        val changerText: String = if (enabled) {
                "&aenabled"
            } else {
                "&cdisabled"
            }
        onToggle(to)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &d$name&7 has been $changerText&7."))
        SettingsFeature.instance.data!!.set("game.scenarios.$id", to)
        SettingsFeature.instance.saveData()
    }

    open fun onToggle(to: Boolean) {}
}
