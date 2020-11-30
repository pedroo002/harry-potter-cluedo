package neptun.jxy1vz.hp_cluedo.ui.fragment.card_pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class CardFragment : Fragment() {

    private lateinit var cardImage: String

    fun setCardImage(imgRes: String) {
        cardImage = imgRes
    }

    companion object {
        fun newInstance(cardImage: String): CardFragment {
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
        loadUrlImageIntoImageView(cardImage, context!!, view.findViewById(R.id.ivLostCard))
        return view
    }
}