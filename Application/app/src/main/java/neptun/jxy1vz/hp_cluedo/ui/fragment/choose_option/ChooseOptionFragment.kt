package neptun.jxy1vz.hp_cluedo.ui.fragment.choose_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.databinding.FragmentChooseOptionBinding
import neptun.jxy1vz.hp_cluedo.domain.handler.DialogDismiss
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.ui.activity.map.MapViewModel
import neptun.jxy1vz.hp_cluedo.ui.fragment.ViewModelListener

class ChooseOptionFragment : Fragment(), ViewModelListener {

    private lateinit var fragmentChooseOptionBinding: FragmentChooseOptionBinding

    private var canStep: Boolean = false
    private lateinit var listener: DialogDismiss

    fun setArgs(steppable: Boolean, l: DialogDismiss) {
        canStep = steppable
        listener = l
    }

    companion object {
        var accusation = false
        var step = false

        fun newInstance(canStep: Boolean, listener: DialogDismiss): ChooseOptionFragment {
            val fragment = ChooseOptionFragment()
            fragment.setArgs(canStep, listener)
            return fragment
        }
    }

    init {
        accusation = false
        step = false

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChooseOptionBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_option, container, false)
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val wizengamotBw = getAssetByTag("resources/map/other/wizengamot_bw.png")!!.url
                val footprintBw = getAssetByTag("resources/map/footprint/footprints_forward1_bw.png")!!.url
                val checkoutCards = getAssetByTag("resources/menu/other/check_out_cards_bw.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(wizengamotBw, context!!, fragmentChooseOptionBinding.ivWizengamot)
                    loadUrlImageIntoImageView(footprintBw, context!!, fragmentChooseOptionBinding.ivStep)
                    loadUrlImageIntoImageView(checkoutCards, context!!, fragmentChooseOptionBinding.ivCards)
                    fragmentChooseOptionBinding.chooseOptionViewModel = ChooseOptionViewModel(fragmentChooseOptionBinding, context!!, canStep, this@ChooseOptionFragment)
                }
            }
        }
        return fragmentChooseOptionBinding.root
    }

    override fun onFinish() {
        if (!step)
            listener.onOptionsDismiss(accusation)
        else
            MapViewModel.enableScrolling()
        MapViewModel.fm.beginTransaction().remove(this).commit()
    }
}