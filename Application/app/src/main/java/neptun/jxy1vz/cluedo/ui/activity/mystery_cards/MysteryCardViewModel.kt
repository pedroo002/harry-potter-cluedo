package neptun.jxy1vz.cluedo.ui.activity.mystery_cards

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.ActivityMysteryCardBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.domain.model.helper.GameModels
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.CardFragment
import neptun.jxy1vz.cluedo.ui.activity.map.MapActivity

class MysteryCardViewModel(
    private val gameModel: GameModels,
    private val context: Context,
    private val playerId: Int,
    private val bind: ActivityMysteryCardBinding,
    private val fm: FragmentManager
) : BaseObservable() {

    private lateinit var player: Player
    private lateinit var adapter: CardPagerAdapter

    init {
        bind.btnGo.isEnabled = false
        GlobalScope.launch(Dispatchers.IO) {
            val playerList = gameModel.loadPlayers()
            withContext(Dispatchers.Main) {
                player = playerList[playerId]
                handOutCardsToPlayers()
            }
        }
    }

    fun openHogwarts() {
        val mapIntent = Intent(context, MapActivity::class.java)
        mapIntent.putExtra(context.getString(R.string.player_id), player.id)
        mapIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mapIntent)
    }

    private suspend fun getMysteryCards(playerIds: List<Int>) {
        val cards = gameModel.db.getMysteryCardsForPlayers(playerIds)

        withContext(Dispatchers.Main) {
            Toast.makeText(context, context.getString(R.string.slide_for_more), Toast.LENGTH_LONG).show()

            val fragmentList = ArrayList<CardFragment>()
            for (card in cards) {
                if (card.second == playerId)
                    fragmentList.add(
                        CardFragment(
                            card.first.imageRes
                        )
                    )
            }
            adapter = CardPagerAdapter(fm, fragmentList)
            bind.cardPager.adapter = adapter

            bind.btnGo.isEnabled = true
        }
    }

    private suspend fun handOutCardsToPlayers() {
        val idList = ArrayList<Int>()
        idList.add(playerId)

        var playerCount = context.getSharedPreferences(context.getString(R.string.game_params_pref), Context.MODE_PRIVATE).getInt(
            context.getString(R.string.player_count_key),
            0
        ) - 1

        for (p in gameModel.playerList) {
            if (p.id != player.id && playerCount > 0) {
                idList.add(p.id)
                playerCount--
            }
        }
        idList.add(-1)
        getMysteryCards(idList)
    }
}