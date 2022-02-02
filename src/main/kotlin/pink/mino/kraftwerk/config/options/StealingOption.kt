package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class StealingOption : ConfigOption(
    "Stealing",
    "Stealing allows people to be with another person and steal stuff like ores from them.",
    "rules",
    "stealing",
    Material.COAL_ORE
)