package neptun.jxy1vz.cluedo.ui.fragment.game_mode

import android.content.Context
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_game_mode.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentGameModeBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class GameModeViewModel(private val bind: FragmentGameModeBinding, private val context: Context, private val listener: ViewModelListener) : BaseObservable() {

    private var gameMode = ""
    private var playerCount = 0

    fun selectPlayerMode(mode: Int) {
        gameMode = context.resources.getStringArray(R.array.playmodes)[mode]
        bind.gameMode = gameMode

        when (mode) {
            0 -> {
                bind.gameModeRoot.ivSinglePlayer.setImageResource(R.drawable.singleplayer_selected)
                bind.gameModeRoot.ivMultiPlayer.setImageResource(R.drawable.multiplayer)
            }
            1 -> {
                bind.gameModeRoot.ivSinglePlayer.setImageResource(R.drawable.singleplayer)
                bind.gameModeRoot.ivMultiPlayer.setImageResource(R.drawable.multiplayer_selected)
            }
        }

        if (gameMode.isNotEmpty() && playerCount != 0)
            bind.gameModeRoot.btnSet.isEnabled = true
    }

    fun selectPlayerCount(count: Int) {
        playerCount = count

        if (count == 3) playerCount = 2

        when (count) {
            3 -> {
                bind.gameModeRoot.ivPlayerCount3.setImageResource(R.drawable.count3selected)
                bind.gameModeRoot.ivPlayerCount4.setImageResource(R.drawable.count4)
                bind.gameModeRoot.ivPlayerCount5.setImageResource(R.drawable.count5)
            }
            4 -> {
                bind.gameModeRoot.ivPlayerCount3.setImageResource(R.drawable.count3)
                bind.gameModeRoot.ivPlayerCount4.setImageResource(R.drawable.count4selected)
                bind.gameModeRoot.ivPlayerCount5.setImageResource(R.drawable.count5)
            }
            5 -> {
                bind.gameModeRoot.ivPlayerCount3.setImageResource(R.drawable.count3)
                bind.gameModeRoot.ivPlayerCount4.setImageResource(R.drawable.count4)
                bind.gameModeRoot.ivPlayerCount5.setImageResource(R.drawable.count5selected)
            }
        }

        if (gameMode.isNotEmpty() && playerCount != 0)
            bind.gameModeRoot.btnSet.isEnabled = true
    }

    fun setGameMode() {
        val pref = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putString(context.resources.getString(R.string.play_mode_key), gameMode)
        editor.putInt(context.resources.getString(R.string.player_count_key), playerCount)
        editor.apply()

        listener.onFinish()
    }

    fun cancel() {
        GameModeFragment.isCanceled = true
        listener.onFinish()
    }
}