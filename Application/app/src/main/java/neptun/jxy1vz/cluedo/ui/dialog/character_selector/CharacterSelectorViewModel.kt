package neptun.jxy1vz.cluedo.ui.dialog.character_selector

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCharacterSelectorBinding
import neptun.jxy1vz.cluedo.ui.mystery_cards.MysteryCardActivity

class CharacterSelectorViewModel(private val bind: DialogCharacterSelectorBinding, private val context: Context, private val listener: CharacterSelectorInterface) : BaseObservable(),
    AdapterView.OnItemSelectedListener {

    interface CharacterSelectorInterface {
        fun onGameStart()
    }

    private var playerId: Int = 0

    init {
        bind.spinnerCharacter.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            context.resources.getStringArray(R.array.characters)
        )
        bind.spinnerCharacter.onItemSelectedListener = this

        val scale = context.resources.displayMetrics.density
        bind.ivCharacterCard.cameraDistance = 8000 * scale
    }

    fun startGame() {
        val mysteryCardIntent = Intent(context, MysteryCardActivity::class.java)
        mysteryCardIntent.putExtra(context.resources.getString(R.string.player_id), playerId)
        mysteryCardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mysteryCardIntent)

        listener.onGameStart()
    }

    private fun setPlayer(id: Int) {
        playerId = id
        val pref = context.getSharedPreferences(context.resources.getString(R.string.game_params_pref), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(context.resources.getString(R.string.player_id_key), id)
        editor.apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        bind.ivCharacterCard.setImageResource(R.drawable.szereplo_hatlap)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        bind.ivCharacterCard.setImageResource(R.drawable.szereplo_hatlap)
        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivCharacterCard)
            start()
            doOnEnd {
                bind.dialogViewModel!!.setPlayer(position)

                val img = when (position) {
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
}