package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class NoBrandingOption : ConfigOption(
    "No Branding",
    "Removes applejuice-related branding.",
    "options",
    "nobranding",
    Material.FLOWER_POT_ITEM
) {}