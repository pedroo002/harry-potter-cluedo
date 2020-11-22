package neptun.jxy1vz.cluedo.ui.fragment.rules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentRulesBinding

class RulesFragment: Fragment() {

    private lateinit var fragmentRulesBinding: FragmentRulesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRulesBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_rules, container, false)
        fragmentRulesBinding.rulesViewModel = RulesViewModel()
        return fragmentRulesBinding.root
    }
}