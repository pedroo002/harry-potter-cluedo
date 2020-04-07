package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.dark_mark

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.animation.doOnEnd
import androidx.databinding.BaseObservable
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDarkCardBinding
import neptun.jxy1vz.cluedo.model.DarkCard
import neptun.jxy1vz.cluedo.model.HelperType
import neptun.jxy1vz.cluedo.model.Player

class DarkCardViewModel(private val bind: DialogDarkCardBinding, private val context: Context, private val player: Player, private val darkCard: DarkCard) : BaseObservable(),
    AdapterView.OnItemSelectedListener {

    private var tools: ArrayList<String> = ArrayList()
    private var spells: ArrayList<String> = ArrayList()
    private var allys: ArrayList<String> = ArrayList()

    private var hatlap: Int = R.drawable.mento_hatlap

    private fun addHelperToArray(name: String, array: ArrayList<String>) {
        array.add(name)
    }

    fun getLoss(): DarkCard? {
        return if (tools.size > 1 || spells.size > 1 || allys.size > 1)
            null
        else
            darkCard
    }

    init {
        tools.add("")
        spells.add("")
        allys.add("")

        if (!player.helperCards.isNullOrEmpty())
            for (helperCard in player.helperCards!!) {
                if (!darkCard.helperIds.isNullOrEmpty() && darkCard.helperIds.contains(helperCard.id))
                    when (helperCard.type) {
                        HelperType.TOOL -> addHelperToArray(helperCard.name, tools)
                        HelperType.SPELL -> addHelperToArray(helperCard.name, spells)
                        HelperType.ALLY -> addHelperToArray(helperCard.name, allys)
                    }
            }
        if (tools.size == 1 && spells.size == 1 && allys.size == 1)
            hatlap = R.drawable.no_mento_hatlap

        bind.ivHelperAgainstDarkCard.setImageResource(hatlap)

        val scale = context.resources.displayMetrics.density
        bind.ivDarkCard.cameraDistance = 8000 * scale

        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(bind.ivDarkCard)
            start()
            doOnEnd {
                bind.ivDarkCard.setImageResource(darkCard.imageRes)
            }
        }

        bind.spinnerTools.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            tools
        )
        bind.spinnerTools.onItemSelectedListener = this
        bind.spinnerTools.setSelection(0)

        bind.spinnerSpells.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            spells
        )
        bind.spinnerSpells.onItemSelectedListener = this
        bind.spinnerSpells.setSelection(0)

        bind.spinnerAllys.adapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            allys
        )
        bind.spinnerAllys.onItemSelectedListener = this
        bind.spinnerAllys.setSelection(0)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val name = parent!!.selectedItem

        if (bind.spinnerTools != parent && bind.spinnerTools.selectedItemId != 0L)
            bind.spinnerTools.setSelection(0)
        if (bind.spinnerSpells != parent && bind.spinnerSpells.selectedItemId != 0L)
            bind.spinnerSpells.setSelection(0)
        if (bind.spinnerAllys != parent && bind.spinnerAllys.selectedItemId != 0L)
            bind.spinnerAllys.setSelection(0)

        if (!player.helperCards.isNullOrEmpty())
            for (card in player.helperCards!!) {
                if (card.name == name) {
                    bind.ivHelperAgainstDarkCard.setImageResource(hatlap)
                    (AnimatorInflater.loadAnimator(
                        context,
                        R.animator.card_flip
                    ) as AnimatorSet).apply {
                        setTarget(bind.ivHelperAgainstDarkCard)
                        start()
                        doOnEnd {
                            bind.ivHelperAgainstDarkCard.setImageResource(card.imageRes)
                        }
                    }
                }
            }
    }
}