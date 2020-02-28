package neptun.jxy1vz.cluedo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R

class TopMapFragment : Fragment() {

    companion object {
        private var topFragment: TopMapFragment? = null

        fun getInstance(): TopMapFragment {
            if (topFragment == null)
                topFragment = TopMapFragment()
            return topFragment!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentTop = inflater.inflate(R.layout.fragment_top_map, container, false)

        return fragmentTop
    }
}