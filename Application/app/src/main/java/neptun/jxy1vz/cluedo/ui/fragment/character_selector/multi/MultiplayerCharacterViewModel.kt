package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.databinding.BaseObservable
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.databinding.FragmentMultiplayerCharacterSelectorBinding
import neptun.jxy1vz.cluedo.domain.model.helper.characterTokenList
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.ui.fragment.ViewModelListener

class MultiplayerCharacterViewModel(
    private val bind: FragmentMultiplayerCharacterSelectorBinding,
    private val context: Context,
    private val lifecycle: LifecycleCoroutineScope,
    private val listener: ViewModelListener
) : BaseObservable() {

    private val guidelines: List<Guideline>
    private val retrofit: RetrofitInstance = RetrofitInstance.getInstance(context)
    private val playerName: String =
        context.getSharedPreferences(context.resources.getString(R.string.player_data_pref), Context.MODE_PRIVATE).getString(context.resources.getString(R.string.player_name_key), "")!!
    private val characterList = context.resources.getStringArray(R.array.characters)
    var playerId = 0

    init {
        val subscribedPlayers = ArrayList<String>()
        lifecycle.launch(Dispatchers.IO) {
            val channelId = context.getSharedPreferences(
                context.resources.getString(R.string.player_data_pref),
                Context.MODE_PRIVATE
            ).getString(context.resources.getString(R.string.channel_id_key), "")

            val channel = retrofit.cluedo.getChannel(channelId!!)
            subscribedPlayers.addAll(channel!!.subscribedUsers)
        }

        val playerCount = context.getSharedPreferences(
            context.resources.getString(R.string.game_params_pref),
            Context.MODE_PRIVATE
        ).getInt(context.resources.getString(R.string.player_count_key), 3)
        guidelines = ArrayList()
        guidelines.add(bind.guidelinePlayer1)
        guidelines.add(bind.guidelinePlayer2)
        guidelines.add(bind.guidelinePlayer3)
        guidelines.add(bind.guidelinePlayer4)
        guidelines.add(bind.guidelinePlayer5)

        for (i in 0 until playerCount) {
            val tvPlayerName = TextView(bind.multiCharacterSelectorRoot.context)
            tvPlayerName.text = subscribedPlayers[i]
            tvPlayerName.setTextColor(Color.WHITE)
            tvPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            tvPlayerName.setTypeface(null, Typeface.BOLD)

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.startToStart = bind.multiCharacterSelectorRoot.id
            layoutParams.topToBottom = guidelines[i].id
            layoutParams.marginStart = 10

            tvPlayerName.layoutParams = layoutParams
            bind.multiCharacterSelectorRoot.addView(tvPlayerName)

            val tvCharacterName = TextView(bind.multiCharacterSelectorRoot.context)
            tvCharacterName.text = ""
            tvCharacterName.setTextColor(Color.WHITE)
            tvCharacterName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            tvCharacterName.setTypeface(null, Typeface.BOLD)

            layoutParams.startToEnd = tvPlayerName.id
            layoutParams.marginStart = 5
            tvCharacterName.layoutParams = layoutParams
            bind.multiCharacterSelectorRoot.addView(tvCharacterName)

            val ivPlayerToken = ImageView(bind.multiCharacterSelectorRoot.context)
            ivPlayerToken.setImageResource(R.drawable.szereplo_token)

            val imgParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
            imgParams.matchConstraintPercentWidth = 0.2f
            imgParams.matchConstraintPercentHeight = 0.2f
            imgParams.startToEnd = tvCharacterName.id
            imgParams.topToTop = tvCharacterName.id
            imgParams.bottomToBottom = tvCharacterName.id
            imgParams.marginStart = 5
            ivPlayerToken.layoutParams = imgParams
            bind.multiCharacterSelectorRoot.addView(ivPlayerToken)

            val tokenImages = ArrayList<ImageView>()
            tokenImages.add(ivPlayerToken)
            for (token in characterTokenList) {
                val ivToken = ImageView(bind.multiCharacterSelectorRoot.context)
                ivToken.setImageResource(token)
                tokenImages.add(ivToken)
            }

            for (j in 1 until tokenImages.size) {
                val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT)
                params.startToEnd = tokenImages[j - 1].id
                params.topToTop = tokenImages[j - 1].id
                params.bottomToBottom = tokenImages[j - 1].id
                params.marginStart = 5
                params.matchConstraintPercentWidth = 0.2f
                params.matchConstraintPercentHeight = 0.2f
                tokenImages[j].layoutParams = params
                tokenImages[j].visibility = ImageView.GONE
                bind.multiCharacterSelectorRoot.addView(tokenImages[j])
            }

            ivPlayerToken.setOnClickListener {
                if (tvPlayerName.text != playerName)
                    return@setOnClickListener
                for (j in 1 until tokenImages.size) {
                    tokenImages[j].visibility = ImageView.VISIBLE
                    tokenImages[j].setOnClickListener {
                        tvCharacterName.text = characterList[j - 1]
                        ivPlayerToken.setImageResource(characterTokenList[j - 1])
                        playerId = j - 1

                        for (k in 1 until tokenImages.size) {
                            tokenImages[k].visibility = ImageView.GONE
                        }
                    }
                }
            }
        }
    }

    fun ready() {
        //TODO: ellenőrizni, hogy mindenki különbözőt választott
        listener.onFinish()
    }
}