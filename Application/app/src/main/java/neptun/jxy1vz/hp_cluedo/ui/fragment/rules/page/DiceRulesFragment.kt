package neptun.jxy1vz.hp_cluedo.ui.fragment.rules.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.data.database.CluedoDatabase
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView

class DiceRulesFragment: Fragment() {

    private lateinit var ivIllustration: ImageView
    private lateinit var ivGryffindor: ImageView
    private lateinit var ivRavenclaw: ImageView
    private lateinit var ivHufflepuff: ImageView
    private lateinit var ivSlytherin: ImageView
    private lateinit var ivHelperCard: ImageView
    private lateinit var ivDarkMark: ImageView
    private lateinit var ivStep: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dice_rules, container, false)

        ivIllustration = root.findViewById(R.id.ivIllustration)
        ivGryffindor = root.findViewById(R.id.ivGryffindor)
        ivRavenclaw = root.findViewById(R.id.ivRavenclaw)
        ivHufflepuff = root.findViewById(R.id.ivHufflepuff)
        ivSlytherin = root.findViewById(R.id.ivSlytherin)
        ivHelperCard = root.findViewById(R.id.ivHelper)
        ivDarkMark = root.findViewById(R.id.ivDarkMark)
        ivStep = root.findViewById(R.id.ivStep)

        return root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.IO) {
            CluedoDatabase.getInstance(context!!).assetDao().apply {
                val diceFragment = getAssetByTag("resources/menu/tutorial/dice_fragment.png")!!.url
                val gryffindor = getAssetByTag("resources/map/dice/dice09_gryffindor.png")!!.url
                val hufflepuff = getAssetByTag("resources/map/dice/dice10_hufflepuff.png")!!.url
                val ravenclaw = getAssetByTag("resources/map/dice/dice11_ravenclaw.png")!!.url
                val slytherin = getAssetByTag("resources/map/dice/dice12_slytherin.png")!!.url
                val helperCard = getAssetByTag("resources/map/dice/dice08_helper_card.png")!!.url
                val darkMark = getAssetByTag("resources/map/dice/dice07_dark_mark.png")!!.url
                val stepTemplate = getAssetByTag("resources/menu/tutorial/step_template.png")!!.url
                withContext(Dispatchers.Main) {
                    loadUrlImageIntoImageView(diceFragment, context!!, ivIllustration)
                    loadUrlImageIntoImageView(gryffindor, context!!, ivGryffindor)
                    loadUrlImageIntoImageView(ravenclaw, context!!, ivRavenclaw)
                    loadUrlImageIntoImageView(hufflepuff, context!!, ivHufflepuff)
                    loadUrlImageIntoImageView(slytherin, context!!, ivSlytherin)
                    loadUrlImageIntoImageView(helperCard, context!!, ivHelperCard)
                    loadUrlImageIntoImageView(darkMark, context!!, ivDarkMark)
                    loadUrlImageIntoImageView(stepTemplate, context!!, ivStep)
                }
            }
        }
    }
}