package pink.mino.kraftwerk.config.options

import org.bukkit.Bukkit
import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class PrivateRoundOption : ConfigOption(
    "Private Round",
    "Enables private round support, slightly tweaking the server to support this.",
    "options",
    "private",
    Material.NAME_TAG
) {
    override fun onToggle(to: Boolean) {
        if (!to) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman load LiteBans")
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman unload LiteBans")
        }
    }
}