package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class AbsorptionBeforePvPOption : ConfigOption(
    "Absorption Before PvP",
    "Players receive Absorption at the beginning of the game that expires at PvP.",
    "specials",
    "abp",
    Material.GOLDEN_APPLE
)