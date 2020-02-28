package neptun.jxy1vz.cluedo.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R

class MiddleMapFragment : Fragment() {

    companion object {
        private var middleFragment: MiddleMapFragment? = null

        fun getInstance(): MiddleMapFragment {
            if (middleFragment == null)
                middleFragment = MiddleMapFragment()
            return middleFragment!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val fragmentMiddle = inflater.inflate(R.layout.fragment_middle_map, container, false)

        return fragmentMiddle
    }
}