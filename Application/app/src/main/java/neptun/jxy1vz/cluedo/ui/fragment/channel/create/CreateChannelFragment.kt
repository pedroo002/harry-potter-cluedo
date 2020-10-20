package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_create_channel.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.domain.util.setNumPicker
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener
import neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.MultiplayerCharacterSelectorFragment

class CreateChannelFragment : Fragment(), ViewModelListener, MenuListener {

    private lateinit var fragmentCreateChannelBinding : FragmentCreateChannelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false)

        setNumPicker(fragmentCreateChannelBinding.root.numAuthKey1, 0, 9, Color.WHITE)
        setNumPicker(fragmentCreateChannelBinding.root.numAuthKey2, 0, 9, Color.WHITE)
        setNumPicker(fragmentCreateChannelBinding.root.numAuthKey3, 0, 9, Color.WHITE)
        setNumPicker(fragmentCreateChannelBinding.root.numAuthKey4, 0, 9, Color.WHITE)

        fragmentCreateChannelBinding.root.txtChannelName.addTextChangedListener {
            fragmentCreateChannelBinding.root.btnCreateChannel.isEnabled = it!!.isNotEmpty()
        }

        fragmentCreateChannelBinding.createChannelViewModel = CreateChannelViewModel(fragmentCreateChannelBinding, context!!, lifecycleScope,  this)
        return fragmentCreateChannelBinding.root
    }

    override fun onFinish() {
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.menuFrame, MultiplayerCharacterSelectorFragment(this)).commit()
    }

    override fun onFragmentClose() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}