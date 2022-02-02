package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import pink.mino.kraftwerk.commands.SplitCommand
import pink.mino.kraftwerk.config.ConfigOption

class SplitEnchantsOption : ConfigOption(
    "Split Enchants",
    "Allows you to split your enchantments using /split.",
    "options",
    "splitenchants",
    Material.ENCHANTED_BOOK,
    false,
    true,
    "split",
    SplitCommand()
)