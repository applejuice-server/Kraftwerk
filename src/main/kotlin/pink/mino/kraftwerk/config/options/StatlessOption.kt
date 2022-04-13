package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class StatlessOption : ConfigOption(
    "Statless",
    "Disables UHC statistics during the game.",
    "options",
    "statless",
    Material.COMPASS
)