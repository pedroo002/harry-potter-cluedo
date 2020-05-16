package neptun.jxy1vz.cluedo.ui.fragment.endgame

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_end_of_game.view.*
import kotlinx.android.synthetic.main.fragment_end_of_game.view.ivPlayer
import kotlinx.android.synthetic.main.fragment_end_of_game.view.ivSuspectToken
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentEndOfGameBinding
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.domain.model.helper.*
import neptun.jxy1vz.cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class EndOfGameViewModel(bind: FragmentEndOfGameBinding, context: Context, suspect: Suspect, private val listener: ViewModelListener) : BaseObservable() {

    private var title = ""
    private var goodSolution = true

    init {
        val playerRes = MapViewModel.playerHandler.getPlayerById(suspect.playerId).card.imageRes
        val playerName = MapViewModel.playerHandler.getPlayerById(suspect.playerId).card.name
        val solution = MapViewModel.gameModels.gameSolution.map { card -> card.name }

        bind.endOfGameRoot.ivPlayer.setImageResource(playerRes)

        title = when {
            solution.contains(suspect.suspect) && solution.contains(suspect.room) && solution.contains(suspect.tool) -> "$playerName ${context.resources.getString(
                R.string.someone_solved_the_mystery)}"
            else -> {
                goodSolution = false
                "$playerName ${context.resources.getString(R.string.wrong_solution)}"
            }
        }
        notifyChange()

        val layoutParams = bind.endOfGameRoot.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomMargin =
            ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * context.resources.displayMetrics.heightPixels / 2).toInt()
        bind.endOfGameRoot.ivSuspectToken.layoutParams = layoutParams

        val roomList = context.resources.getStringArray(R.array.rooms)
        val toolList = context.resources.getStringArray(R.array.tools)
        val suspectList = context.resources.getStringArray(R.array.suspects)

        if (goodSolution) {
            bind.endOfGameRoot.tvTitle.setTextColor(Color.GREEN)
            for (solutionParameter in solution) {
                when {
                    suspectList.contains(solutionParameter) -> bind.endOfGameRoot.ivSuspectToken.setImageResource(
                        suspectTokens[suspectList.indexOf(solutionParameter)]
                    )
                    toolList.contains(solutionParameter) -> bind.endOfGameRoot.ivToolToken.setImageResource(
                        toolTokens[toolList.indexOf(solutionParameter)]
                    )
                    else -> bind.endOfGameRoot.ivRoomToken.setImageResource(
                        roomTokens[roomList.indexOf(
                            solutionParameter
                        )]
                    )
                }
            }
        }
        else {
            bind.endOfGameRoot.tvTitle.setTextColor(Color.RED)
            val correctSuspect = Suspect(-1, "", "", "")
            for (solutionParameter in solution) {
                when {
                    suspectList.contains(solutionParameter) -> correctSuspect.suspect = solutionParameter
                    toolList.contains(solutionParameter) -> correctSuspect.tool = solutionParameter
                    else -> correctSuspect.room = solutionParameter
                }
            }

            bind.endOfGameRoot.ivSuspectToken.setImageResource(suspectTokensBW[suspectList.indexOf(suspect.suspect)])
            bind.endOfGameRoot.ivToolToken.setImageResource(toolTokensBW[toolList.indexOf(suspect.tool)])
            bind.endOfGameRoot.ivRoomToken.setImageResource(roomTokensBW[roomList.indexOf(suspect.room)])

            bind.endOfGameRoot.tvGoodSolution.text = context.getString(R.string.the_correct_solution)
            bind.endOfGameRoot.ivGoodSuspectToken.setImageResource(suspectTokens[suspectList.indexOf(correctSuspect.suspect)])
            bind.endOfGameRoot.ivGoodToolToken.setImageResource(toolTokens[toolList.indexOf(correctSuspect.tool)])
            bind.endOfGameRoot.ivGoodRoomToken.setImageResource(roomTokens[roomList.indexOf(correctSuspect.room)])

            (bind.ivGoodSuspectToken.layoutParams as ConstraintLayout.LayoutParams).bottomMargin = layoutParams.bottomMargin
        }
    }

    fun getTitle(): String {
        return title
    }

    fun quit() {
        listener.onFinish()
    }
}