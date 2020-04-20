package neptun.jxy1vz.cluedo.ui.dialog.loss_dialog.card_loss

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.DialogCardLossBinding
import neptun.jxy1vz.cluedo.domain.model.HelperCard
import neptun.jxy1vz.cluedo.domain.model.LossType
import neptun.jxy1vz.cluedo.ui.card_pager.adapter.CardPagerAdapter
import neptun.jxy1vz.cluedo.ui.card_pager.fragment.CardFragment

class CardLossDialog(private val playerIdx: Int, private val helperCards: List<HelperCard>, private val loss_type: LossType, private val listener: CardLossDialogListener) : DialogFragment() {

    interface CardLossDialogListener {
        fun throwCard(playerId: Int, card: HelperCard)
    }

    private lateinit var dialogCardLossBinding: DialogCardLossBinding
    private lateinit var fragmentList: MutableList<CardFragment>
    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogCardLossBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogCardLossBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context!!),
            R.layout.dialog_card_loss,
            null,
            false
        )

        fragmentList = ArrayList()
        for (card in helperCards) {
            fragmentList.add(CardFragment(card.imageRes))
        }
        adapter = CardPagerAdapter(childFragmentManager, fragmentList)

        val title = when (loss_type) {
            LossType.TOOL -> R.string.tool_loss
            LossType.SPELL -> R.string.spell_loss
            else -> R.string.ally_loss
        }

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogCardLossBinding.root).setTitle(title)
            .setNeutralButton(R.string.throw_card) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }

    override fun onResume() {
        super.onResume()
        dialogCardLossBinding.vpCards.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.throwCard(playerIdx, helperCards[dialogCardLossBinding.vpCards.currentItem])
        super.onDismiss(dialog)
    }
}