package neptun.jxy1vz.cluedo.ui.dialog.endgame

import android.content.Context
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R

class EndOfGameViewModel(playerName: String, correct: Boolean, context: Context) : BaseObservable() {

    private var result: String = if (correct)
        playerName + context.getString(R.string.someone_solved_the_mystery)
    else
        playerName + context.getString(R.string.wrong_solution)

    fun getResult(): String {
        return result
    }

}