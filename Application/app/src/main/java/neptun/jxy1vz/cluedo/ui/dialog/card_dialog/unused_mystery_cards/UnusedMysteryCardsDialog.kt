package neptun.jxy1vz.cluedo.ui.dialog.card_dialog.unused_mystery_cards

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
import neptun.jxy1vz.cluedo.databinding.DialogUnusedMysteryCardsBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.ui.card_pager.adapter.CardPagerAdapter
import neptun.jxy1vz.cluedo.ui.card_pager.fragment.CardFragment

class UnusedMysteryCardsDialog(private val listener: DialogDismiss, private val cardList: List<MysteryCard>) : DialogFragment() {

    companion object {
        const val TAG = "DIALOG_UNUSED_MYSTERY_CARDS"
    }

    private lateinit var dialogUnusedMysteryCardsBinding: DialogUnusedMysteryCardsBinding
    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogUnusedMysteryCardsBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogUnusedMysteryCardsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_unused_mystery_cards, null, false)
        dialogUnusedMysteryCardsBinding.dialogUnusedMysteryCardsViewModel = UnusedMysteryCardsViewModel()

        val fragmentList = ArrayList<CardFragment>()
        for (card in cardList) {
            fragmentList.add(CardFragment(card.imageRes))
        }
        adapter = CardPagerAdapter(childFragmentManager, fragmentList)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogUnusedMysteryCardsBinding.root).setTitle(R.string.unused_cards).setNeutralButton(R.string.ok) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onResume() {
        super.onResume()
        dialogUnusedMysteryCardsBinding.vpCards.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onCardRevealDismiss()
        super.onDismiss(dialog)
    }
}