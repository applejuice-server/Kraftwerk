package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class RollarcoasteringOption : ConfigOption(
    "Rollarcoastering",
    "Allows people to mine upwards and downwards in a straight line to find ores.",
    "rules",
    "rollarcoastering",
    Material.NETHER_BRICK_STAIRS
)