package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import neptun.jxy1vz.cluedo.R
import neptun.jxy1vz.cluedo.domain.model.helper.characterTokenList
import neptun.jxy1vz.cluedo.network.api.RetrofitInstance
import neptun.jxy1vz.cluedo.network.model.PlayerDomainModel
import neptun.jxy1vz.cluedo.network.pusher.PusherInstance

class PlayerListAdapter(private val playerList: ArrayList<PlayerDomainModel>, private val currentPlayer: String, private val listener: EventTriggerListener) : RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {

    interface EventTriggerListener {
        fun onSelect(playerName: String, characterName: String, tokenSource: Int)
    }

    private lateinit var characterList: Array<String>

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.tvPlayerName)
        val characterName: TextView = itemView.findViewById(R.id.tvCharacterName)
        val characterImage: ImageView = itemView.findViewById(R.id.ivCharacterIcon)

        private val ginny: ImageView = itemView.findViewById(R.id.ivGinny)
        private val harry: ImageView = itemView.findViewById(R.id.ivHarry)
        private val hermione: ImageView = itemView.findViewById(R.id.ivHermione)
        private val ron: ImageView = itemView.findViewById(R.id.ivRon)
        private val luna: ImageView = itemView.findViewById(R.id.ivLuna)
        private val neville: ImageView = itemView.findViewById(R.id.ivNeville)

        init {
            val tokenImages = listOf(ginny, harry, hermione, ron, luna, neville)

            characterImage.setOnClickListener {
                if (playerName.text != currentPlayer) {
                    Snackbar.make(itemView, "Saját magadnak válassz karaktert!", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                tokenImages.forEach {listItem ->
                    listItem.visibility = ImageView.VISIBLE
                    listItem.setOnClickListener { token ->
                        characterName.visibility = TextView.GONE
                        val idx = tokenImages.indexOf(token)
                        doSelection(idx, characterName, characterImage, layoutPosition, tokenImages, listItem)
                        tokenImages.forEach {
                            it.visibility = ImageView.GONE
                        }
                    }
                }
            }
        }

        fun setViewHolder(character: String, token: Int) {
            characterName.text = character
            characterImage.setImageResource(token)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        characterList = parent.context.resources.getStringArray(R.array.characters)

        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playerName.text = playerList[position].playerName
        holder.characterName.text = ""
        holder.characterImage.setImageResource(R.drawable.szereplo_token)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            val payload = payloads[0] as ArrayList<*>
            holder.setViewHolder(payload[0].toString(), payload[1].toString().toInt())
        }
    }

    override fun getItemCount(): Int = playerList.size

    fun doSelection(idx: Int, tvChar: TextView, ivChar: ImageView, pos: Int, tokenImages: List<ImageView>, listItem: ImageView) {
        tvChar.text = characterList[idx]
        tvChar.visibility = TextView.VISIBLE
        ivChar.setImageResource(characterTokenList[idx])
        playerList[pos].playerId = tokenImages.indexOf(listItem)
        playerList[pos].selectedCharacter = tvChar.text.toString()

        listener.onSelect(currentPlayer, tvChar.text.toString(), characterTokenList[idx])
    }

    fun updatePlayerSelection(playerName: String, characterName: String, token: Int) {
        val item = playerList.find { player -> player.playerName == playerName }!!
        item.selectedCharacter = characterName
        item.playerId = characterList.indexOf(characterName)
        val payloads: MutableList<String> = ArrayList()
        payloads.add(characterName)
        payloads.add(token.toString())
        notifyItemRangeChanged(playerList.indexOf(item), 1, payloads)
    }

    fun getCurrentPlayer(): PlayerDomainModel {
        return playerList.find { player -> player.playerName == currentPlayer }!!
    }

    fun areSelectionsDifferent(): Boolean {
        return playerList.distinct().size == playerList.size
    }
}