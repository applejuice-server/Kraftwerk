package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class DeathLightningOption : ConfigOption(
    "Death Lightning",
    "Toggles lightning when a player dies.",
    "options",
    "deathlightning",
    Material.IRON_BARDING
)