package neptun.jxy1vz.hp_cluedo.ui.fragment.endgame

import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_end_of_game.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentEndOfGameBinding
import neptun.jxy1vz.hp_cluedo.domain.model.Suspect
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class EndOfGameViewModel(
    bind: FragmentEndOfGameBinding,
    context: Context,
    suspect: Suspect,
    private val listener: ViewModelListener
) : BaseObservable() {

    private var title = ""
    private var goodSolution = true

    private lateinit var roomTokens: List<String>
    private lateinit var toolTokens: List<String>
    private lateinit var suspectTokens: List<String>

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                roomTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_ROOM_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                toolTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_TOOL_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
                suspectTokens =
                    getAssetsByPrefix(AssetPrefixes.MYSTERY_SUSPECT_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
            }

            val player = MapViewModel.playerHandler.getPlayerById(suspect.playerId)
            val playerName =
                if (MapViewModel.isGameModeMulti()) CluedoDatabase.getInstance(context).playerDao()
                    .getPlayers()!!
                    .find { p -> p.characterName == player.card.name }!!.playerName else player.card.name
            val playerRes = player.card.imageRes
            val solution = MapViewModel.gameModels.gameSolution.map { card -> card.name }

            withContext(Dispatchers.Main) {
                loadUrlImageIntoImageView(playerRes, context, bind.endOfGameRoot.ivPlayer)

                title = when {
                    solution.contains(suspect.suspect) && solution.contains(suspect.room) && solution.contains(
                        suspect.tool
                    ) -> {
                        if (suspect.playerId != MapViewModel.mPlayerId) "$playerName ${
                            context.resources.getString(
                                R.string.someone_solved_the_mystery
                            )
                        }" else MapViewModel.mContext!!.resources.getString(R.string.good_solution_self_message)
                    }
                    else -> {
                        goodSolution = false
                        if (suspect.playerId != MapViewModel.mPlayerId) "$playerName ${
                            context.resources.getString(
                                R.string.wrong_solution
                            )
                        }" else MapViewModel.mContext!!.resources.getString(R.string.wrong_solution_self_message)
                    }
                }
                notifyChange()

                EndOfGameFragment.goodSolution = goodSolution

                val layoutParams =
                    bind.endOfGameRoot.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomMargin =
                    ((bind.ivSuspectToken.layoutParams as ConstraintLayout.LayoutParams).matchConstraintPercentHeight * context.resources.displayMetrics.heightPixels / 2).toInt()
                bind.endOfGameRoot.ivSuspectToken.layoutParams = layoutParams

                val roomList = context.resources.getStringArray(R.array.rooms)
                val toolList = context.resources.getStringArray(R.array.tools)
                val suspectList = context.resources.getStringArray(R.array.suspects)

                if (goodSolution) {
                    bind.endOfGameRoot.tvTitle.setTextColor(Color.GREEN)
                    solution.forEach { solutionParameter ->
                        when {
                            suspectList.contains(solutionParameter) -> loadUrlImageIntoImageView(
                                suspectTokens[suspectList.indexOf(solutionParameter) * 2],
                                context,
                                bind.endOfGameRoot.ivSuspectToken
                            )
                            toolList.contains(solutionParameter) -> loadUrlImageIntoImageView(
                                toolTokens[toolList.indexOf(solutionParameter) * 2],
                                context,
                                bind.endOfGameRoot.ivToolToken
                            )
                            else -> loadUrlImageIntoImageView(roomTokens[roomList.indexOf(solutionParameter) * 2], context, bind.endOfGameRoot.ivRoomToken)
                        }
                    }
                } else {
                    bind.endOfGameRoot.tvTitle.setTextColor(Color.RED)
                    val correctSuspect = Suspect(-1, "", "", "")
                    solution.forEach { solutionParameter ->
                        when {
                            suspectList.contains(solutionParameter) -> correctSuspect.suspect =
                                solutionParameter
                            toolList.contains(solutionParameter) -> correctSuspect.tool =
                                solutionParameter
                            else -> correctSuspect.room = solutionParameter
                        }
                    }

                    loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(suspect.suspect) * 2 + 1], context, bind.endOfGameRoot.ivSuspectToken)
                    loadUrlImageIntoImageView(toolTokens[toolList.indexOf(suspect.tool) * 2 + 1], context, bind.endOfGameRoot.ivToolToken)
                    loadUrlImageIntoImageView(roomTokens[roomList.indexOf(suspect.room) * 2 + 1], context, bind.endOfGameRoot.ivRoomToken)

                    if (!MapViewModel.isGameModeMulti()) {
                        bind.endOfGameRoot.tvGoodSolution.text =
                            context.getString(R.string.the_correct_solution)
                        loadUrlImageIntoImageView(suspectTokens[suspectList.indexOf(correctSuspect.suspect) * 2], context, bind.endOfGameRoot.ivGoodSuspectToken)
                        loadUrlImageIntoImageView(toolTokens[toolList.indexOf(correctSuspect.tool) * 2], context, bind.endOfGameRoot.ivGoodToolToken)
                        loadUrlImageIntoImageView(roomTokens[roomList.indexOf(correctSuspect.room) * 2], context, bind.endOfGameRoot.ivGoodRoomToken)

                        (bind.ivGoodSuspectToken.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
                            layoutParams.bottomMargin
                    } else
                        bind.endOfGameRoot.btnQuit.text = context.resources.getString(R.string.ok)
                }
            }
        }
    }

    fun getTitle(): String {
        return title
    }

    fun quit() {
        listener.onFinish()
    }
}