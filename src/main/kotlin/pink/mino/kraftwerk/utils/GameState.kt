package pink.mino.kraftwerk.utils

import pink.mino.kraftwerk.features.ConfigFeature
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
            ConfigFeature.instance.data?.set("game.state", state.name.uppercase(Locale.getDefault()))
            ConfigFeature.instance.saveData()
        }

        var currentState: GameState? = null

    }
}