package neptun.jxy1vz.cluedo.ui.fragment.character_selector.single

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_character_selector.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCharacterSelectorBinding
import neptun.jxy1vz.cluedo.domain.model.helper.characterTokenList
import neptun.jxy1vz.cluedo.domain.model.helper.characterTokenListBW
import neptun.jxy1vz.cluedo.ui.activity.mystery_cards.MysteryCardActivity
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

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

    init {
        val scale = context.resources.displayMetrics.density
        bind.ivCharacterCard.cameraDistance = 8000 * scale
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
                ivList[i].setImageResource(characterTokenList[i])
            else
                ivList[i].setImageResource(characterTokenListBW[i])
        }

        bind.ivCharacterCard.setImageResource(R.drawable.szereplo_hatlap)
        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivCharacterCard)
            start()
            doOnEnd {
                val img = when (id) {
                    0 -> R.drawable.szereplo_ginny
                    1 -> R.drawable.szereplo_harry
                    2 -> R.drawable.szereplo_hermione
                    3 -> R.drawable.szereplo_ron
                    4 -> R.drawable.szereplo_luna
                    else -> R.drawable.szereplo_neville
                }
                bind.ivCharacterCard.setImageResource(img)
            }
        }
    }

    fun cancel() {
        listener.onFinish()
    }
}