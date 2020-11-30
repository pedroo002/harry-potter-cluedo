package neptun.jxy1vz.hp_cluedo.ui.fragment.cards.mystery

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_unused_mystery_cards.view.*
import neptun.jxy1vz.hp_cluedo.databinding.FragmentUnusedMysteryCardsBinding
import neptun.jxy1vz.hp_cluedo.domain.model.card.MysteryCard
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class UnusedMysteryCardsViewModel(
    bind: FragmentUnusedMysteryCardsBinding,
    context: Context,
    cardList: List<MysteryCard>,
    private val listener: ViewModelListener
) : BaseObservable() {

    init {
        when (cardList.size) {
            2 -> {
                loadUrlImageIntoImageView(cardList[0].imageRes, context, bind.unusedCardsRoot.ivCardLeft)
                loadUrlImageIntoImageView(cardList[1].imageRes, context, bind.unusedCardsRoot.ivCardRight)

                val newParamsLeft = bind.unusedCardsRoot.ivCardLeft.layoutParams as ConstraintLayout.LayoutParams
                val newParamsRight = bind.unusedCardsRoot.ivCardRight.layoutParams as ConstraintLayout.LayoutParams

                newParamsLeft.endToEnd = bind.unusedCardsRoot.guidelineCenter.id
                newParamsRight.startToStart = bind.unusedCardsRoot.guidelineCenter.id

                bind.unusedCardsRoot.ivCardLeft.layoutParams = newParamsLeft
                bind.unusedCardsRoot.ivCardRight.layoutParams = newParamsRight
            }
            3 -> {
                loadUrlImageIntoImageView(cardList[0].imageRes, context, bind.unusedCardsRoot.ivCardLeft)
                loadUrlImageIntoImageView(cardList[1].imageRes, context, bind.unusedCardsRoot.ivCardCenter)
                loadUrlImageIntoImageView(cardList[2].imageRes, context, bind.unusedCardsRoot.ivCardRight)
            }
        }
    }

    fun close() {
        listener.onFinish()
    }
}