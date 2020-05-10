package neptun.jxy1vz.cluedo.ui.fragment.cards.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentHelperCardBinding
import neptun.jxy1vz.cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class HelperCardFragment(private val cardResource: Int, private val listener: DialogDismiss) : Fragment(),
    ViewModelListener {

    private lateinit var fragmentHelperCardBinding: FragmentHelperCardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentHelperCardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_helper_card, container, false)
        fragmentHelperCardBinding.helperCardViewModel = HelperCardViewModel(fragmentHelperCardBinding, context!!, cardResource, this)
        return fragmentHelperCardBinding.root
    }

    override fun onFinish() {
        listener.onHelperCardDismiss()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commit()
    }
}