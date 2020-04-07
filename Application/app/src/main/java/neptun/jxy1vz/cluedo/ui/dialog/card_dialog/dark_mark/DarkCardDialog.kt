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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogDarkCardBinding
import neptun.jxy1vz.cluedo.model.DarkCard
import neptun.jxy1vz.cluedo.model.HelperType
import neptun.jxy1vz.cluedo.model.Player

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

    private var hatlap: Int = R.drawable.mento_hatlap

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

        dialogDarkCardBinding.ivHelperAgainstDarkCard.setImageResource(hatlap)

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
        dialogDarkCardBinding.spinnerTools.setSelection(0)

        dialogDarkCardBinding.spinnerSpells.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            spells
        )
        dialogDarkCardBinding.spinnerSpells.onItemSelectedListener = this
        dialogDarkCardBinding.spinnerSpells.setSelection(0)

        dialogDarkCardBinding.spinnerAllys.adapter = ArrayAdapter<String>(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            allys
        )
        dialogDarkCardBinding.spinnerAllys.onItemSelectedListener = this
        dialogDarkCardBinding.spinnerAllys.setSelection(0)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_DialogWhenLarge)
            .setView(dialogDarkCardBinding.root)
            .setTitle(resources.getString(R.string.sotet_jegy)).setNeutralButton(
                resources.getString(R.string.ok)
            ) { dialog, _ ->
                if (tools.size > 1 || spells.size > 1 || allys.size > 1)
                    listener.getLoss(null)
                else
                    listener.getLoss(darkCard)
                dialog.dismiss()
            }.create()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val name = parent!!.selectedItem

        if (dialogDarkCardBinding.spinnerTools != parent && dialogDarkCardBinding.spinnerTools.selectedItemId != 0L)
            dialogDarkCardBinding.spinnerTools.setSelection(0)
        if (dialogDarkCardBinding.spinnerSpells != parent && dialogDarkCardBinding.spinnerSpells.selectedItemId != 0L)
            dialogDarkCardBinding.spinnerSpells.setSelection(0)
        if (dialogDarkCardBinding.spinnerAllys != parent && dialogDarkCardBinding.spinnerAllys.selectedItemId != 0L)
            dialogDarkCardBinding.spinnerAllys.setSelection(0)

        if (!player.helperCards.isNullOrEmpty())
            for (card in player.helperCards!!) {
                if (card.name == name) {
                    dialogDarkCardBinding.ivHelperAgainstDarkCard.setImageResource(hatlap)
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