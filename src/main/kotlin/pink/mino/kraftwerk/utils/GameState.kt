package pink.mino.kraftwerk.utils

import pink.mino.kraftwerk.features.SettingsFeature
import java.util.*

enum class GameState {
    LOBBY, WAITING, INGAME;

    companion object {
        /**
         * Gets the current state.
         * @return The state
         */
        fun setState(state: GameState) {
            currentState = state
            SettingsFeature.instance.data?.set("game.state", state.name.uppercase(Locale.getDefault()))
            SettingsFeature.instance.saveData()
        }

        var currentState: GameState? = null

    }
}