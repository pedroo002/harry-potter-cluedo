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
            context.resources.getStringArray(neptun.jxy1vz.cluedo.R.array.characters)
        )
        bind.spinnerCharacter.onItemSelectedListener = this

        val scale = context.resources.displayMetrics.density
        bind.ivCharacterCard.cameraDistance = 8000 * scale
    }

    fun startGame() {
        val mysteryCardIntent = Intent(context, MysteryCardActivity::class.java)
        mysteryCardIntent.putExtra("Player ID", playerId)
        mysteryCardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(mysteryCardIntent)

        listener.onGameStart()
    }

    private fun setPlayer(id: Int) {
        playerId = id
        val pref = context.getSharedPreferences("Game params", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt("player_id", id)
        editor.apply()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        bind.ivCharacterCard.setImageResource(neptun.jxy1vz.cluedo.R.drawable.szereplo_hatlap)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        bind.ivCharacterCard.setImageResource(neptun.jxy1vz.cluedo.R.drawable.szereplo_hatlap)
        (AnimatorInflater.loadAnimator(context, neptun.jxy1vz.cluedo.R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivCharacterCard)
            start()
            doOnEnd {
                bind.dialogViewModel!!.setPlayer(position)

                val img = when (position) {
                    0 -> neptun.jxy1vz.cluedo.R.drawable.szereplo_ginny
                    1 -> neptun.jxy1vz.cluedo.R.drawable.szereplo_harry
                    2 -> neptun.jxy1vz.cluedo.R.drawable.szereplo_hermione
                    3 -> neptun.jxy1vz.cluedo.R.drawable.szereplo_ron
                    4 -> neptun.jxy1vz.cluedo.R.drawable.szereplo_luna
                    else -> neptun.jxy1vz.cluedo.R.drawable.szereplo_neville
                }
                bind.ivCharacterCard.setImageResource(img)
            }
        }
    }
}