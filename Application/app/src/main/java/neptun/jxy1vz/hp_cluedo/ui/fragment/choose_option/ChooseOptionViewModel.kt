package neptun.jxy1vz.hp_cluedo.ui.fragment.choose_option

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BaseObservable
import kotlinx.android.synthetic.main.fragment_choose_option.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentChooseOptionBinding
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class ChooseOptionViewModel(private val bind: FragmentChooseOptionBinding, private val context: Context, private val canStep: Boolean, private val listener: ViewModelListener) : BaseObservable() {

    private var selectedOption = -1

    private lateinit var wizengamot: String
    private lateinit var wizengamotBW: String
    private lateinit var step: String
    private lateinit var stepBW: String
    private lateinit var cards: String
    private lateinit var cardsBW: String

    init {
        GlobalScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context).assetDao().apply {
                wizengamotBW = getAssetByTag("resources/map/other/wizengamot_bw.png")!!.url
                wizengamot = getAssetByTag("resources/map/other/wizengamot.png")!!.url
                cardsBW = getAssetByTag("resources/menu/other/check_out_cards_bw.png")!!.url
                cards = getAssetByTag("resources/menu/other/check_out_cards.png")!!.url
                stepBW = getAssetByTag("resources/map/footprint/footprints_forward1_bw.png")!!.url
                step = getAssetByTag("resources/map/footprint/footprints_forward1.png")!!.url
                if (!canStep) {
                    withContext(Dispatchers.Main) {
                        bind.optionRoot.ivStep.visibility = ImageView.GONE
                        bind.optionRoot.tvStep.visibility = TextView.GONE
                    }
                }
            }
        }
    }

    fun selectOption(num: Int) {
        if (!this::wizengamot.isInitialized)
            return
        when (num) {
            0 -> {
                loadUrlImageIntoImageView(wizengamot, context, bind.optionRoot.ivWizengamot)
                loadUrlImageIntoImageView(cardsBW, context, bind.optionRoot.ivCards)
                loadUrlImageIntoImageView(stepBW, context, bind.optionRoot.ivStep)
                ChooseOptionFragment.accusation = true
                ChooseOptionFragment.step = false
            }
            1 -> {
                loadUrlImageIntoImageView(wizengamotBW, context, bind.optionRoot.ivWizengamot)
                loadUrlImageIntoImageView(cards, context, bind.optionRoot.ivCards)
                loadUrlImageIntoImageView(stepBW, context, bind.optionRoot.ivStep)
                ChooseOptionFragment.accusation = false
                ChooseOptionFragment.step = false
            }
            2 -> {
                if (canStep) {
                    loadUrlImageIntoImageView(wizengamotBW, context, bind.optionRoot.ivWizengamot)
                    loadUrlImageIntoImageView(cardsBW, context, bind.optionRoot.ivCards)
                    loadUrlImageIntoImageView(step, context, bind.optionRoot.ivStep)
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