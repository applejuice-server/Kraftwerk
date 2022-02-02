package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption

class PokeholingOption : ConfigOption(
   "Pokeholing",
   "Allows people to pokeholes while stripmining to find ores.",
   "rules",
   "pokeholing",
   Material.COAL
)