package neptun.jxy1vz.cluedo.ui.fragment.channel.create

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_create_channel.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCreateChannelBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class CreateChannelFragment : Fragment(), ViewModelListener {

    private lateinit var fragmentCreateChannelBinding : FragmentCreateChannelBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateChannelBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_channel, container, false)

        fragmentCreateChannelBinding.root.numAuthKey1.minValue = 0
        fragmentCreateChannelBinding.root.numAuthKey1.maxValue = 9
        fragmentCreateChannelBinding.root.numAuthKey1.textColor = Color.WHITE

        fragmentCreateChannelBinding.root.numAuthKey2.minValue = 0
        fragmentCreateChannelBinding.root.numAuthKey2.maxValue = 9
        fragmentCreateChannelBinding.root.numAuthKey2.textColor = Color.WHITE

        fragmentCreateChannelBinding.root.numAuthKey3.minValue = 0
        fragmentCreateChannelBinding.root.numAuthKey3.maxValue = 9
        fragmentCreateChannelBinding.root.numAuthKey3.textColor = Color.WHITE

        fragmentCreateChannelBinding.root.numAuthKey4.minValue = 0
        fragmentCreateChannelBinding.root.numAuthKey4.maxValue = 9
        fragmentCreateChannelBinding.root.numAuthKey4.textColor = Color.WHITE

        fragmentCreateChannelBinding.root.txtChannelName.addTextChangedListener {
            fragmentCreateChannelBinding.root.btnCreateChannel.isEnabled = it!!.isNotEmpty()
        }

        fragmentCreateChannelBinding.createChannelViewModel = CreateChannelViewModel(fragmentCreateChannelBinding, context!!, this)
        return fragmentCreateChannelBinding.root
    }

    override fun onFinish() {
        TODO("Not yet implemented")
    }
}