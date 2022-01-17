package pink.mino.kraftwerk.features.options

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk


class ConfigOptionHandler {
    companion object {
        var configOptions = ArrayList<ConfigOption>()

        fun setup() {
            addOption(AbsorptionOption())
            configOptions.sortWith(Comparator.comparing(ConfigOption::name))
        }

        fun getOptions(): ArrayList<ConfigOption> {
            return configOptions
        }

        fun getOption(name: String?): ConfigOption? {
            for (configOption in configOptions) {
                if (configOption.name == name) {
                    return configOption
                }
            }
            return null
        }

        private fun addOption(configOption: ConfigOption) {
            configOptions.add(configOption)
            Bukkit.getPluginManager().registerEvents(configOption, JavaPlugin.getPlugin(Kraftwerk::class.java))
        }
    }
}