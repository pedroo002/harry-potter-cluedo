package neptun.jxy1vz.cluedo.ui.fragment.card_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import neptun.jxy1vz.cluedo.R

class CardFragment(private val cardImage: Int) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lost_card, container, false)
        view.findViewById<ImageView>(R.id.ivLostCard).setImageResource(cardImage)
        return view
    }
}