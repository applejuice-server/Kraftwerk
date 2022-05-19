package pink.mino.kraftwerk.config

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.options.*


class ConfigOptionHandler {
    companion object {
        var configOptions = ArrayList<ConfigOption>()

        fun setup() {
            addOption(AbsorptionOption())
            addOption(AntiStoneOption())
            addOption(GoldenHeadsOption())
            addOption(NotchAppleOption())
            addOption(HorsesOption())
            addOption(SplitEnchantsOption())
            addOption(BookshelvesOption())
            addOption(FireWeaponsOption())
            addOption(AntiBurnOption())
            addOption(PearlDamageOption())
            addOption(EnderPearlCooldownOption())
            addOption(StatlessOption())
            addOption(DoubleArrowsOption())

            addOption(CrossteamingOption())
            addOption(StalkingOption())
            addOption(StealingOption())
            addOption(SkybasingOption())
            addOption(RunningAtMeetupOption())

            addOption(RollarcoasteringOption())
            addOption(StripminingOption())
            addOption(PokeholingOption())

            addOption(NerfedQuartzOption())
            addOption(TierIIOption())
            addOption(StrengthPotionOption())
            addOption(SplashPotionsOption())

            configOptions.sortWith(Comparator.comparing(ConfigOption::name))
        }

        fun getOptions(): ArrayList<ConfigOption> {
            return configOptions
        }

        fun getOptionsByCategory(category: String): ArrayList<ConfigOption> {
            val list = ArrayList<ConfigOption>()
            for (configOption in configOptions) {
                if (configOption.category === category) {
                    list.add(configOption)
                }
            }
            return list
        }

        fun getOption(id: String?): ConfigOption? {
            for (configOption in configOptions) {
                if (configOption.id == id) {
                    return configOption
                }
            }
            return null
        }

        private fun addOption(configOption: ConfigOption) {
            configOptions.add(configOption)
            Bukkit.getPluginManager().registerEvents(configOption, JavaPlugin.getPlugin(Kraftwerk::class.java))
            if (configOption.command) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand(configOption.commandName).executor = configOption.executor
            }
        }
    }
}