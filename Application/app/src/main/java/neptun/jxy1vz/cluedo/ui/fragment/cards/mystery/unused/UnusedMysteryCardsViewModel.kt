package neptun.jxy1vz.cluedo.ui.fragment.cards.mystery.unused

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_unused_mystery_cards.view.*
import neptun.jxy1vz.cluedo.databinding.FragmentUnusedMysteryCardsBinding
import neptun.jxy1vz.cluedo.domain.model.MysteryCard
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class UnusedMysteryCardsViewModel(
    bind: FragmentUnusedMysteryCardsBinding,
    cardList: List<MysteryCard>,
    private val listener: ViewModelListener
) : BaseObservable() {

    init {
        when (cardList.size) {
            2 -> {
                bind.unusedCardsRoot.ivCardLeft.setImageResource(cardList[0].imageRes)
                bind.unusedCardsRoot.ivCardRight.setImageResource(cardList[1].imageRes)

                val newParamsLeft = bind.unusedCardsRoot.ivCardLeft.layoutParams as ConstraintLayout.LayoutParams
                val newParamsRight = bind.unusedCardsRoot.ivCardRight.layoutParams as ConstraintLayout.LayoutParams

                newParamsLeft.endToEnd = bind.unusedCardsRoot.guidelineCenter.id
                newParamsRight.startToStart = bind.unusedCardsRoot.guidelineCenter.id

                bind.unusedCardsRoot.ivCardLeft.layoutParams = newParamsLeft
                bind.unusedCardsRoot.ivCardRight.layoutParams = newParamsRight
            }
            3 -> {
                bind.unusedCardsRoot.ivCardLeft.setImageResource(cardList[0].imageRes)
                bind.unusedCardsRoot.ivCardCenter.setImageResource(cardList[1].imageRes)
                bind.unusedCardsRoot.ivCardRight.setImageResource(cardList[2].imageRes)
            }
        }
    }

    fun close() {
        listener.onFinish()
    }
}