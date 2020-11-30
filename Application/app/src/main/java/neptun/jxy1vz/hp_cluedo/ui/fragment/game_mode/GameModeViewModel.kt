package neptun.jxy1vz.hp_cluedo.ui.fragment.game_mode

import android.content.Context
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_game_mode.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentGameModeBinding
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class GameModeViewModel(private val bind: FragmentGameModeBinding, private val context: Context, private val listener: ViewModelListener) : BaseObservable() {

    private var gameMode = ""
    private var playerCount = 0

    private lateinit var single: String
    private lateinit var singleSelected: String
    private lateinit var multi: String
    private lateinit var multiSelected: String

    private lateinit var count3: String
    private lateinit var count4: String
    private lateinit var count5: String
    private lateinit var count3Selected: String
    private lateinit var count4Selected: String
    private lateinit var count5Selected: String

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                single = getAssetByTag("resources/menu/other/singleplayer.png")!!.url
                multi = getAssetByTag("resources/menu/other/multiplayer.png")!!.url
                singleSelected = getAssetByTag("resources/menu/other/singleplayer_selected.png")!!.url
                multiSelected = getAssetByTag("resources/menu/other/multiplayer_selected.png")!!.url
                count3 = getAssetByTag("resources/menu/other/count3.png")!!.url
                count4 = getAssetByTag("resources/menu/other/count4.png")!!.url
                count5 = getAssetByTag("resources/menu/other/count5.png")!!.url
                count3Selected = getAssetByTag("resources/menu/other/count3selected.png")!!.url
                count4Selected = getAssetByTag("resources/menu/other/count4selected.png")!!.url
                count5Selected = getAssetByTag("resources/menu/other/count5selected.png")!!.url
            }
        }
    }

    fun selectPlayerMode(mode: Int) {
        if (!this::multiSelected.isInitialized)
            return
        gameMode = context.resources.getStringArray(R.array.playmodes)[mode]
        bind.gameMode = gameMode

        when (mode) {
            0 -> {
                loadUrlImageIntoImageView(singleSelected, context, bind.gameModeRoot.ivSinglePlayer)
                loadUrlImageIntoImageView(multi, context, bind.gameModeRoot.ivMultiPlayer)
            }
            1 -> {
                loadUrlImageIntoImageView(single, context, bind.gameModeRoot.ivSinglePlayer)
                loadUrlImageIntoImageView(multiSelected, context, bind.gameModeRoot.ivMultiPlayer)
            }
        }

        if (gameMode.isNotEmpty() && playerCount != 0)
            bind.gameModeRoot.btnSet.isEnabled = true
    }

    fun selectPlayerCount(count: Int) {
        if (!this::count5Selected.isInitialized)
            return

        playerCount = count
        when (count) {
            3 -> {
                loadUrlImageIntoImageView(count3Selected, context, bind.gameModeRoot.ivPlayerCount3)
                loadUrlImageIntoImageView(count4, context, bind.gameModeRoot.ivPlayerCount4)
                loadUrlImageIntoImageView(count5, context, bind.gameModeRoot.ivPlayerCount5)
            }
            4 -> {
                loadUrlImageIntoImageView(count3, context, bind.gameModeRoot.ivPlayerCount3)
                loadUrlImageIntoImageView(count4Selected, context, bind.gameModeRoot.ivPlayerCount4)
                loadUrlImageIntoImageView(count5, context, bind.gameModeRoot.ivPlayerCount5)
            }
            5 -> {
                loadUrlImageIntoImageView(count3, context, bind.gameModeRoot.ivPlayerCount3)
                loadUrlImageIntoImageView(count4, context, bind.gameModeRoot.ivPlayerCount4)
                loadUrlImageIntoImageView(count5Selected, context, bind.gameModeRoot.ivPlayerCount5)
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