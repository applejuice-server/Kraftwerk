package pink.mino.kraftwerk.features.options

import org.bukkit.Material

class StripminingOption: ConfigOption(
    "Stripmining",
    "Allows people to mine in a straight line under y-32",
    "rules",
    "stripmining",
    Material.GOLD_PICKAXE
)