package neptun.jxy1vz.cluedo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R

class BottomMapFragment : Fragment() {

    companion object {
        private var bottomFragment: BottomMapFragment? = null

        fun getInstance(): BottomMapFragment {
            if (bottomFragment == null)
                bottomFragment = BottomMapFragment()
            return bottomFragment!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentBottom = inflater.inflate(R.layout.fragment_bottom_map, container, false)

        return fragmentBottom
    }
}