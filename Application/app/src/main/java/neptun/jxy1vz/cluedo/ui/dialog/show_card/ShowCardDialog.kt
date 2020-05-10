package neptun.jxy1vz.cluedo.ui.dialog.show_card

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
import neptun.jxy1vz.cluedo.databinding.DialogShowCardBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.domain.model.Suspect
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.adapter.CardPagerAdapter
import neptun.jxy1vz.cluedo.ui.fragment.card_pager.CardFragment

class ShowCardDialog(private val suspect: Suspect, private val forWho: String, private val cardList: List<MysteryCard>, private val listener: DialogDismiss) : DialogFragment() {

    companion object {
        const val TAG = "DIALOG_SHOW_CARD"
    }

    private lateinit var dialogShowCardBinding: DialogShowCardBinding
    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogShowCardBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogShowCardBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_show_card, null, false)
        dialogShowCardBinding.dialogShowCardViewModel = ShowCardViewModel()

        val fragmentList = ArrayList<CardFragment>()
        for (card in cardList) {
            fragmentList.add(
                CardFragment(
                    card.imageRes
                )
            )
        }
        adapter = CardPagerAdapter(childFragmentManager, fragmentList)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog).setView(dialogShowCardBinding.root).setTitle("${context!!.resources.getString(R.string.show_card)} neki: $forWho").setNeutralButton(R.string.show) {
            dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onResume() {
        super.onResume()
        dialogShowCardBinding.vpCards.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onCardShowDismiss(suspect, cardList[dialogShowCardBinding.vpCards.currentItem])
        super.onDismiss(dialog)
    }
}