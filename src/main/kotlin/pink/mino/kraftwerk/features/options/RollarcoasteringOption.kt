package pink.mino.kraftwerk.features.options

import org.bukkit.Material

class RollarcoasteringOption : ConfigOption(
    "Rollarcoastering",
    "Allows people to mine upwards and downwards in a straight line to find ores.",
    "rules",
    "rollarcoastering",
    Material.NETHER_BRICK_STAIRS
)