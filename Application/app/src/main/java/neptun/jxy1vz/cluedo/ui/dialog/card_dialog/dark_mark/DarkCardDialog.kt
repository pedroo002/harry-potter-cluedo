package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.dark_mark

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDarkCardBinding
import neptun.jxy1vz.cluedo.model.DarkCard
import neptun.jxy1vz.cluedo.model.HelperType
import neptun.jxy1vz.cluedo.model.LossType
import neptun.jxy1vz.cluedo.model.Player
import neptun.jxy1vz.cluedo.model.helper.darkCards

class DarkCardDialog(
    private val player: Player,
    private val darkCard: DarkCard,
    private val listener: DarkCardDialogListener
) : DialogFragment(),
    AdapterView.OnItemSelectedListener {

    interface DarkCardDialogListener {
        fun getLoss(card: DarkCard?)
    }

    private lateinit var dialogDarkCardBinding: DialogDarkCardBinding

    private var tools: ArrayList<String> = ArrayList()
    private var spells: ArrayList<String> = ArrayList()
    private var allys: ArrayList<String> = ArrayList()

    init {
        if (!player.helperCards.isNullOrEmpty())
            for (helperCard in player.helperCards!!) {
                if (!darkCard.helperIds.isNullOrEmpty() && darkCard.helperIds.contains(helperCard.id))
                    when (helperCard.type) {
                        HelperType.TOOL -> addHelperToArray(helperCard.name, tools)
                        HelperType.SPELL -> addHelperToArray(helperCard.name, spells)
                        HelperType.ALLY -> addHelperToArray(helperCard.name, allys)
                    }
            }
    }

    private fun addHelperToArray(name: String, array: ArrayList<String>) {
        array.add(name)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogDarkCardBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_dark_card,
            null,
            false
        )
        dialogDarkCardBinding.darkCardDialogViewModel = DarkCardViewModel()

        dialogDarkCardBinding.ivHelperAgainstDarkCard.isVisible = false

        val scale = resources.displayMetrics.density
        dialogDarkCardBinding.ivDarkCard.cameraDistance = 8000 * scale

        (AnimatorInflater.loadAnimator(context, R.animator.card_flip) as AnimatorSet).apply {
            setTarget(dialogDarkCardBinding.ivDarkCard)
            start()
            doOnEnd {
                dialogDarkCardBinding.ivDarkCard.setImageResource(darkCard.imageRes)
            }
        }

        dialogDarkCardBinding.spinnerTools.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            tools
        )
        dialogDarkCardBinding.spinnerTools.onItemSelectedListener = this

        dialogDarkCardBinding.spinnerSpells.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            spells
        )
        dialogDarkCardBinding.spinnerSpells.onItemSelectedListener = this

        dialogDarkCardBinding.spinnerAllys.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            allys
        )
        dialogDarkCardBinding.spinnerAllys.onItemSelectedListener = this

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge)
            .setView(dialogDarkCardBinding.root)
            .setTitle(resources.getString(R.string.sotet_jegy)).setNeutralButton(
                resources.getString(R.string.ok)
            ) { dialog, _ ->
                if (tools.isNotEmpty() || spells.isNotEmpty() || allys.isNotEmpty())
                    listener.getLoss(null)
                else
                    listener.getLoss(darkCard)
                dialog.dismiss()
            }.create()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        dialogDarkCardBinding.ivHelperAgainstDarkCard.isVisible = false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val name = parent!!.selectedItem
        dialogDarkCardBinding.ivHelperAgainstDarkCard.isVisible = true

        if (!player.helperCards.isNullOrEmpty())
            for (card in player.helperCards!!) {
                if (card.name == name) {
                    dialogDarkCardBinding.ivHelperAgainstDarkCard.setImageResource(R.drawable.mento_hatlap)
                    (AnimatorInflater.loadAnimator(
                        context,
                        R.animator.card_flip
                    ) as AnimatorSet).apply {
                        setTarget(dialogDarkCardBinding.ivHelperAgainstDarkCard)
                        start()
                        doOnEnd {
                            dialogDarkCardBinding.ivHelperAgainstDarkCard.setImageResource(card.imageRes)
                        }
                    }
                }
            }
    }
}