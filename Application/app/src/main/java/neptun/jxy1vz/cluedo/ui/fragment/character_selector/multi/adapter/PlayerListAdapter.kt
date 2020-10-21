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
import neptun.jxy1vz.cluedo.network.model.PlayerDomainModel

class PlayerListAdapter(private val playerList: ArrayList<PlayerDomainModel>, private val currentPlayer: String) : RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {

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
                        val idx = tokenImages.indexOf(token)
                        characterName.text = characterList[idx]
                        characterImage.setImageResource(characterTokenList[idx])
                        playerList[layoutPosition].playerId = tokenImages.indexOf(listItem)
                        playerList[layoutPosition].selectedCharacter = characterName.text.toString()
                        tokenImages.forEach {
                            it.visibility = ImageView.GONE
                        }
                    }
                }
            }
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

    override fun getItemCount(): Int = playerList.size

    fun getCurrentPlayer(): PlayerDomainModel {
        return playerList.find { player -> player.playerName == currentPlayer }!!
    }

    fun areSelectionsDifferent(): Boolean {
        return playerList.distinct().size == playerList.size
    }
}