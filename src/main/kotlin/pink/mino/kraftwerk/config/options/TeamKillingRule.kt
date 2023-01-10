package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class TeamKillingRule : ConfigOption(
    "Team Killing",
    "Allows people to kill the player they are teamed with.",
    "rules",
    "teamkilling",
    Material.REDSTONE_ORE
)