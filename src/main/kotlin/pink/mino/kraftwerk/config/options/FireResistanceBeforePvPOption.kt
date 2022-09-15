package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class FireResistanceBeforePvPOption : ConfigOption(
    "Fire Resistance Before PvP",
    "Players receive Fire Resistance at the beginning of the game that expires at PvP.",
    "specials",
    "frbp",
    Material.BLAZE_POWDER
)