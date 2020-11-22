package neptun.jxy1vz.cluedo.ui.fragment.rules.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R

class IncriminationRulesFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_incrimination_rules, container, false)
    }
}