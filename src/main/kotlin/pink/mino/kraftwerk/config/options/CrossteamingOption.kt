package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class CrossteamingOption : ConfigOption(
    "Crossteaming",
    "Allows people to team with each other even if they're not on the same team.",
    "rules",
    "crossteaming",
    Material.IRON_SWORD
)