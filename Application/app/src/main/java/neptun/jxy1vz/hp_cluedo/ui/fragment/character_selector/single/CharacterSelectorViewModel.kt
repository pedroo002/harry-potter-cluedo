package neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.single

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_character_selector.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.data.database.model.AssetPrefixes
import neptun.jxy1vz.hp_cluedo.data.database.model.string
import neptun.jxy1vz.hp_cluedo.databinding.FragmentCharacterSelectorBinding
import neptun.jxy1vz.hp_cluedo.domain.model.card.PlayerCard
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.domain.util.toDomainModel
import neptun.jxy1vz.hp_cluedo.ui.activity.mystery_cards.MysteryCardActivity
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class CharacterSelectorViewModel(private val bind: FragmentCharacterSelectorBinding, private val context: Context, private val listener: ViewModelListener) : BaseObservable() {

    private var playerId = -1

    private val ivList = listOf(
        bind.ivGinny,
        bind.ivHarry,
        bind.ivHermione,
        bind.ivRon,
        bind.ivLuna,
        bind.ivNeville
    )

    private lateinit var playerTokens: List<String>
    private lateinit var playerCards: List<PlayerCard>

    init {
        GlobalScope.launch(Dispatchers.IO) {
            playerTokens = CluedoDatabase.getInstance(context).assetDao().getAssetsByPrefix(AssetPrefixes.PLAYER_TOKENS.string())!!.map { assetDBmodel -> assetDBmodel.url }
            playerCards = CluedoDatabase.getInstance(context).cardDao().getCardsByType(
                neptun.jxy1vz.hp_cluedo.data.database.model.CardType.PLAYER.string())!!.map { dbModel -> dbModel.toDomainModel(context) as PlayerCard }
            withContext(Dispatchers.Main) {
                loadUrlImageIntoImageView(playerTokens[1], context, bind.ivGinny)
                loadUrlImageIntoImageView(playerTokens[3], context, bind.ivHarry)
                loadUrlImageIntoImageView(playerTokens[5], context, bind.ivHermione)
                loadUrlImageIntoImageView(playerTokens[7], context, bind.ivRon)
                loadUrlImageIntoImageView(playerTokens[9], context, bind.ivLuna)
                loadUrlImageIntoImageView(playerTokens[11], context, bind.ivNeville)
                loadUrlImageIntoImageView(playerCards[0].verso, context, bind.ivCharacterCard)
                val scale = context.resources.displayMetrics.density
                bind.ivCharacterCard.cameraDistance = 8000 * scale
            }
        }
    }

    fun startGame() {
        val mysteryCardIntent = Intent(context, MysteryCardActivity::class.java)
        mysteryCardIntent.putExtra(context.resources.getString(R.string.player_id), playerId)
        mysteryCardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mysteryCardIntent)

        listener.onFinish()
    }

    fun setPlayer(id: Int) {
        bind.characterSelectorRoot.btnStart.isEnabled = true

        playerId = id
        val pref = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(context.resources.getString(R.string.player_id_key), id)
        editor.apply()

        for (i in 0..5) {
            if (i == id)
                loadUrlImageIntoImageView(playerTokens[2 * i], context, ivList[i])
            else
                loadUrlImageIntoImageView(playerTokens[2 * i + 1], context, ivList[i])
        }

        loadUrlImageIntoImageView(playerCards[id].verso, context, bind.ivCharacterCard)
        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivCharacterCard)
            start()
            doOnEnd {
                loadUrlImageIntoImageView(playerCards[id].imageRes, context, bind.ivCharacterCard)
            }
        }
    }

    fun cancel() {
        listener.onFinish()
    }
}