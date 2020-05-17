package neptun.jxy1vz.cluedo.ui.fragment.choose_option

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BaseObservable
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_choose_option.view.*
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentChooseOptionBinding
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

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
        if (canStep || num != 2)
            selectedOption = num
    }

    fun finish() {
        if (selectedOption == -1) {
            Snackbar.make(bind.optionRoot, context.getString(R.string.choose_an_option), Snackbar.LENGTH_SHORT).show()
            return
        }
        listener.onFinish()
    }
}