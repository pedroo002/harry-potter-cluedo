package neptun.jxy1vz.cluedo.ui.dialog.player_dies

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
import neptun.jxy1vz.cluedo.databinding.DialogPlayerDiesBinding
import neptun.jxy1vz.cluedo.domain.model.Player
import neptun.jxy1vz.cluedo.ui.card_pager.adapter.CardPagerAdapter
import neptun.jxy1vz.cluedo.ui.card_pager.fragment.CardFragment
import neptun.jxy1vz.cluedo.ui.map.DialogDismiss

class PlayerDiesDialog(private val player: Player, private val listener: DialogDismiss) :
    DialogFragment() {

    private lateinit var dialogPlayerDiesBinding: DialogPlayerDiesBinding
    private lateinit var adapter: CardPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogPlayerDiesBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogPlayerDiesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_player_dies,
            null,
            false
        )
        dialogPlayerDiesBinding.dialogPlayerDiesViewModel = PlayerDiesViewModel()

        val fragmentList = ArrayList<CardFragment>()
        for (card in player.mysteryCards) {
            fragmentList.add(CardFragment(card.imageRes))
        }
        adapter = CardPagerAdapter(childFragmentManager, fragmentList)

        return AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Light_Dialog)
            .setView(dialogPlayerDiesBinding.root)
            .setTitle("${player.card.name} elvesztette házpontjait, kiesett")
            .setMessage("Rejtély kártyái ezek voltak:").setNeutralButton(R.string.ok) { dialog, _ ->
            dialog.dismiss()
        }.create()
    }

    override fun onResume() {
        super.onResume()
        dialogPlayerDiesBinding.vpCards.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener.onPlayerDiesDismiss(player)
        super.onDismiss(dialog)
    }
}