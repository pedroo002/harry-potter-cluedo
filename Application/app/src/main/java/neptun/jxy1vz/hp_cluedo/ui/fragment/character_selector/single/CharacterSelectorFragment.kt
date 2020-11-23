package neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.single

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentCharacterSelectorBinding
import neptun.jxy1vz.hp_cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class CharacterSelectorFragment : Fragment(),
    ViewModelListener {

    private lateinit var listener: MenuListener

    fun setListener(l: MenuListener) {
        listener = l
    }

    private lateinit var fragmentCharacterSelectorBinding: FragmentCharacterSelectorBinding

    companion object {
        var isCanceled = false

        fun newInstance(listener: MenuListener): CharacterSelectorFragment {
            val fragment = CharacterSelectorFragment()
            fragment.setListener(listener)
            return fragment
        }
    }

    init {
        isCanceled = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCharacterSelectorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_character_selector, container, false)
        fragmentCharacterSelectorBinding.characterSelectorViewModel = CharacterSelectorViewModel(fragmentCharacterSelectorBinding, context!!, this)
        return fragmentCharacterSelectorBinding.root
    }

    override fun onFinish() {
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
        listener.onFragmentClose()
    }
}