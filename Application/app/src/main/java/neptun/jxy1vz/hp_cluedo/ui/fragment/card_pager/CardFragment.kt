package neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R

class CardFragment : Fragment() {

    private var cardImage: Int = 0

    fun setCardImage(imgRes: Int) {
        cardImage = imgRes
    }

    companion object {
        fun newInstance(cardImage: Int): CardFragment {
            val fragment = CardFragment()
            fragment.setCardImage(cardImage)
            return fragment
        }
    }

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