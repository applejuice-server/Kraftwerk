package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class PortalTrappingOption : ConfigOption(
    "Portal Trapping",
    "Portal trapping allows players to trap nether portals.",
    "rules",
    "portaltrapping",
    Material.PISTON_BASE
)