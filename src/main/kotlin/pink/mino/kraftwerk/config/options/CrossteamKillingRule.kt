package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class CrossteamKillingRule : ConfigOption(
    "Crossteam Killing",
    "Allows people to kill the player they are crossteaming with.",
    "rules",
    "scumballing",
    Material.IRON_BARDING
)