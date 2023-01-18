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
            addScenario(FastGetawayScenario())
            addScenario(DoubleOresScenario())
            addScenario(LootCratesScenario())
            addScenario(GigaDrillScenario())
            addScenario(BareBonesScenario())
            addScenario(DiamondlessScenario())
            addScenario(SwitcherooScenario())
            addScenario(BetaZombiesScenario())
            addScenario(MonstersIncScenario())
            addScenario(AvengersScenario())
            addScenario(TeamInventoryScenario.instance)
            addScenario(GoldenRetrieverScenario())
            addScenario(OPLootCratesScenario())
            addScenario(MolesScenario.instance)
            addScenario(WebCageScenario())
            addScenario(NoCleanScenario.instance)
            addScenario(GraveRobbersScenario())
            addScenario(FalloutScenario())
            addScenario(TimeBombScenario())
            addScenario(NicholasCageScenario())
            addScenario(BatsScenario())
            addScenario(ExtremeSkyHighScenario())
            addScenario(WeakestLinkScenario())
            addScenario(EnchantedDeathScenario())
            addScenario(InfiniteEnchanterScenario())
            addScenario(BloodDiamondsScenario())
            addScenario(VeinMinerScenario())
            addScenario(VillagerMadnessScenario())
            addScenario(ParanoiaScenario())
            addScenario(RewardingLongshotsScenario())
            addScenario(BigCrackScenario())
            addScenario(UndergroundParallelScenario())
            addScenario(SkyOresScenario())
            addScenario(EggsScenario())
            addScenario(AuctionScenario())
            addScenario(ChampionsScenario())
            addScenario(DoNotDisturbScenario())
            addScenario(CreeperPongScenario())
            addScenario(BowlessScenario())
            addScenario(NoFallScenario())
            addScenario(LoveAtFirstSightScenario())
            addScenario(GenieScenario())
            addScenario(UnendurableHealingScenario())
            addScenario(AssaultAndBatteryScenario())
            addScenario(GappleRouletteScenario())
            addScenario(SuperheroesScenario())
            addScenario(KrenzinatorScenario())
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
        }
    }
}