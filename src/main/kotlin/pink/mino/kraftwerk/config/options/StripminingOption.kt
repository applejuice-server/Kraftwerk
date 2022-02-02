package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class StripminingOption: ConfigOption(
    "Stripmining",
    "Allows people to mine in a straight line under y-32",
    "rules",
    "stripmining",
    Material.GOLD_PICKAXE
)