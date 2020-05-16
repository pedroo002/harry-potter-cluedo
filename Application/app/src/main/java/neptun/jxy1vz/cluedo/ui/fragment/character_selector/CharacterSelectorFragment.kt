package neptun.jxy1vz.cluedo.ui.fragment.character_selector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentCharacterSelectorBinding
import neptun.jxy1vz.cluedo.ui.activity.menu.MenuListener
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class CharacterSelectorFragment(private val listener: MenuListener) : Fragment(),
    ViewModelListener {

    private lateinit var fragmentCharacterSelectorBinding: FragmentCharacterSelectorBinding

    companion object {
        var isCanceled = false
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