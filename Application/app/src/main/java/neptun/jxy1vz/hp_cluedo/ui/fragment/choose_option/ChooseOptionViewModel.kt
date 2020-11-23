package neptun.jxy1vz.hp_cluedo.ui.fragment.choose_option

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_choose_option.view.*
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.databinding.FragmentChooseOptionBinding
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class ChooseOptionViewModel(private val bind: FragmentChooseOptionBinding, private val context: Context, private val canStep: Boolean, private val listener: ViewModelListener) : BaseObservable() {

    private var selectedOption = -1

    init {
        if (!canStep) {
            bind.optionRoot.ivStep.visibility = ImageView.GONE
            bind.optionRoot.tvStep.visibility = TextView.GONE
        }
    }

    fun selectOption(num: Int) {
        when (num) {
            0 -> {
                bind.optionRoot.ivWizengamot.setImageResource(R.drawable.wizengamot)
                bind.optionRoot.ivCards.setImageResource(R.drawable.check_out_cards_bw)
                bind.optionRoot.ivStep.setImageResource(R.drawable.footprints_forward1_bw)
                ChooseOptionFragment.accusation = true
                ChooseOptionFragment.step = false
            }
            1 -> {
                bind.optionRoot.ivWizengamot.setImageResource(R.drawable.wizengamot_bw)
                bind.optionRoot.ivCards.setImageResource(R.drawable.check_out_cards)
                bind.optionRoot.ivStep.setImageResource(R.drawable.footprints_forward1_bw)
                ChooseOptionFragment.accusation = false
                ChooseOptionFragment.step = false
            }
            2 -> {
                if (canStep) {
                    bind.optionRoot.ivWizengamot.setImageResource(R.drawable.wizengamot_bw)
                    bind.optionRoot.ivCards.setImageResource(R.drawable.check_out_cards_bw)
                    bind.optionRoot.ivStep.setImageResource(R.drawable.footprints_forward1)
                    ChooseOptionFragment.accusation = false
                    ChooseOptionFragment.step = true
                }
            }
        }
        if (canStep || num != 2) {
            selectedOption = num
            bind.optionRoot.btnOk.isEnabled = true
        }
    }

    fun finish() {
        listener.onFinish()
    }
}