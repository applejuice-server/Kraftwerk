package pink.mino.kraftwerk.features.options

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.Listener
import pink.mino.kraftwerk.features.Settings
import pink.mino.kraftwerk.utils.Chat

abstract class ConfigOption(
    var name: String,
    var description: String,
    var category: String,
    var id: String,
    var material: Material,
    var enabled: Boolean = false
): Listener {

    init {
        if (!enabled) enabled = false
    }

    fun toggle() {
        enabled = !enabled
        val changerText: String = if (enabled) {
            "&aenabled"
        } else {
            "&cdisabled"
        }
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &c$name&7 has been $changerText&7."))
        Settings.instance.data!!.set("game.$category.$id", enabled)
        Settings.instance.saveData()
    }

    @JvmName("setEnabled1")
    fun setEnabled(to: Boolean) {
        enabled = to
        val changerText: String = if (enabled) {
            "&aenabled"
        } else {
            "&cdisabled"
        }
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &c$name&7 has been $changerText&7."))
        Settings.instance.data!!.set("game.$category.$id", to)
        Settings.instance.saveData()
    }

}
