package pink.mino.kraftwerk.features.options

import org.bukkit.Material

class CrossteamingOption : ConfigOption(
    "Crossteaming",
    "Allows people to team with each other even if they're not on the same team.",
    "rules",
    "crossteaming",
    Material.IRON_SWORD
)