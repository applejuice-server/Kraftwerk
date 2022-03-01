package pink.mino.kraftwerk.scenarios

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.list.*

class ScenarioHandler {
    companion object {
        val scenarios = ArrayList<Scenario>()

        fun setup() {
            addScenario(CutCleanScenario())
            addScenario(TimberScenario())
            addScenario(HasteyBoysScenario())
            addScenario(FlowerPowerScenario())
            addScenario(SkyHighScenario())
            addScenario(GoneFishingScenario())
            addScenario(BleedingSweetsScenario())
            addScenario(SiphonScenario())
            scenarios.sortWith(Comparator.comparing(Scenario::name))
        }

        @JvmName("getScenarios1")
        fun getScenarios(): ArrayList<Scenario> {
            return scenarios
        }

        fun getActiveScenarios(): ArrayList<Scenario> {
            val active = ArrayList<Scenario>()
            for (scenario in scenarios) {
                if (scenario.enabled) {
                    active.add(scenario)
                }
            }
            return active
        }

        fun getScenario(id: String?): Scenario? {
            for (scenario in scenarios) {
                if (scenario.id == id) {
                    return scenario
                }
            }
            return null
        }

        private fun addScenario(scenario: Scenario) {
            scenarios.add(scenario)
            Bukkit.getPluginManager().registerEvents(scenario, JavaPlugin.getPlugin(Kraftwerk::class.java))
            if (scenario.command) {
                JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand(scenario.commandName).executor = scenario.executor
            }
        }
    }
}