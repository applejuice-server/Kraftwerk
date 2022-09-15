package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class PortalCampingOption : ConfigOption(
    "Portal Camping",
    "Allows players to camp in/near nether portals",
    "rules",
    "portalcamping",
    Material.OBSIDIAN
)