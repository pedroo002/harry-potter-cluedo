package neptun.jxy1vz.cluedo.ui.dialog.endgame

import androidx.databinding.BaseObservable

class EndOfGameViewModel(playerName: String, correct: Boolean) : BaseObservable() {

    private var result: String = if (correct)
        "$playerName megoldotta a rejtélyt."
    else
        "$playerName rossz megoldást adott."

    fun getResult(): String {
        return result
    }

}