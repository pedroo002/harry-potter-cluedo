package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentMultiplayerCharacterSelectorBinding
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuViewModel
import neptun.jxy1vz.cluedo.ui.activity.mystery_cards.MysteryCardActivity
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class MultiplayerCharacterSelectorFragment(private val listener: MenuListener) : Fragment(), ViewModelListener {
    private lateinit var fragmentMultiplayerCharacterSelectorBinding: FragmentMultiplayerCharacterSelectorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMultiplayerCharacterSelectorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_multiplayer_character_selector, container, false)
        fragmentMultiplayerCharacterSelectorBinding.characterSelectorViewModel = MultiplayerCharacterViewModel(fragmentMultiplayerCharacterSelectorBinding, context!!, lifecycleScope, this)
        return fragmentMultiplayerCharacterSelectorBinding.root
    }

    override fun onFinish() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        listener.onFragmentClose()

        val mysteryCardIntent = Intent(context, MysteryCardActivity::class.java)
        mysteryCardIntent.putExtra(context!!.resources.getString(R.string.player_id), fragmentMultiplayerCharacterSelectorBinding.characterSelectorViewModel!!.playerId)
        mysteryCardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context!!.startActivity(mysteryCardIntent)
    }
}