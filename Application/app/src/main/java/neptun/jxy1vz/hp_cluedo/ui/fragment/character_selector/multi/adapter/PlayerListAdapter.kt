package neptun.jxy1vz.hp_cluedo.ui.fragment.character_selector.multi.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import neptun.jxy1vz.hp_cluedo.R
import neptun.jxy1vz.hp_cluedo.domain.util.loadUrlImageIntoImageView
import neptun.jxy1vz.hp_cluedo.network.model.player.PlayerDomainModel

class PlayerListAdapter(private val context: Context, private val playerList: ArrayList<PlayerDomainModel>, private val playerTokens: List<String>, private val currentPlayer: String, private val listener: AdapterListener) : RecyclerView.Adapter<PlayerListAdapter.ViewHolder>() {

    interface AdapterListener {
        fun onSelect(playerName: String, characterName: String)
        fun onReady(playerName: String)
        fun refreshSelection(idx: Int = -1)
    }

    private lateinit var characterList: Array<String>
    private var ready = false

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

        private var listOpened = false

        init {
            val tokenImages = listOf(ginny, harry, hermione, ron, luna, neville)

            characterImage.setOnClickListener {
                if (ready)
                    return@setOnClickListener

                if (listOpened) {
                    tokenImages.forEach {
                        it.visibility = ImageView.GONE
                    }
                    listOpened = false
                    characterName.visibility = TextView.VISIBLE
                    return@setOnClickListener
                }

                if (playerName.text != currentPlayer) {
                    Snackbar.make(itemView, "Saját magadnak válassz karaktert!", Snackbar.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                listOpened = true
                tokenImages.forEach {listItem ->
                    val layoutParams = listItem.layoutParams as LinearLayout.LayoutParams
                    layoutParams.width = it.measuredWidth
                    layoutParams.height = it.measuredHeight
                    listItem.layoutParams = layoutParams

                    listItem.visibility = ImageView.VISIBLE
                    characterName.visibility = TextView.GONE
                    listItem.setOnClickListener { token ->
                        val idx = tokenImages.indexOf(token)
                        doSelection(idx, characterName, characterImage, layoutPosition, tokenImages, listItem)
                        tokenImages.forEach {
                            it.visibility = ImageView.GONE
                        }
                        listOpened = false
                    }
                }
            }
        }

        fun setViewHolder(character: String, token: String) {
            characterName.text = character
            loadUrlImageIntoImageView(token, context, characterImage)
        }

        fun setColor(color: Int) {
            characterName.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        characterList = parent.context.resources.getStringArray(R.array.characters)

        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_list_item, parent, false)
        loadUrlImageIntoImageView(playerTokens.last(), context, view.findViewById(R.id.ivCharacterIcon))
        loadUrlImageIntoImageView(playerTokens[0], context, view.findViewById(R.id.ivGinny))
        loadUrlImageIntoImageView(playerTokens[2], context, view.findViewById(R.id.ivHarry))
        loadUrlImageIntoImageView(playerTokens[4], context, view.findViewById(R.id.ivHermione))
        loadUrlImageIntoImageView(playerTokens[6], context, view.findViewById(R.id.ivRon))
        loadUrlImageIntoImageView(playerTokens[8], context, view.findViewById(R.id.ivLuna))
        loadUrlImageIntoImageView(playerTokens[10], context, view.findViewById(R.id.ivNeville))
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playerName.text = playerList[position].playerName
        holder.characterName.text = ""
        loadUrlImageIntoImageView(playerTokens.last(), context, holder.characterImage)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position)
        else {
            val payload = payloads[0] as ArrayList<*>
            if (payload.size > 1)
                holder.setViewHolder(payload[0].toString(), payload[1].toString())
            else
                holder.setColor(payload[0].toString().toInt())
        }
    }

    override fun getItemCount(): Int = playerList.size

    private fun doSelection(idx: Int, tvChar: TextView, ivChar: ImageView, pos: Int, tokenImages: List<ImageView>, listItem: ImageView) {
        tvChar.text = characterList[idx]
        tvChar.visibility = TextView.VISIBLE
        loadUrlImageIntoImageView(playerTokens[idx * 2], context, ivChar)
        playerList[pos].playerId = tokenImages.indexOf(listItem)
        playerList[pos].selectedCharacter = tvChar.text.toString()

        listener.onSelect(currentPlayer, tvChar.text.toString())
    }

    fun setReady() {
        ready = true
    }

    fun updatePlayerSelection(playerName: String, characterName: String, token: String) {
        val item = playerList.find { player -> player.playerName == playerName }!!
        item.selectedCharacter = characterName
        item.playerId = characterList.indexOf(characterName)
        val payloads: MutableList<String> = ArrayList()
        payloads.add(characterName)
        payloads.add(token)
        notifyItemRangeChanged(playerList.indexOf(item), 1, payloads)
    }

    fun getCurrentPlayer(): PlayerDomainModel {
        return playerList.find { player -> player.playerName == currentPlayer }!!
    }

    fun getPlayer(playerName: String): PlayerDomainModel {
        return playerList.find { player -> player.playerName == playerName }!!
    }

    fun getTokenFromCharacterName(characterName: String): String {
        return playerTokens[characterList.indexOf(characterName) * 2]
    }

    fun areSelectionsDifferent(): Boolean {
        return playerList.map { player -> (player.selectedCharacter) }.distinct().size == playerList.size
    }

    fun deletePlayer(playerName: String) {
        val item = playerList.find { player -> player.playerName == playerName }
        val pos = playerList.indexOf(item)
        playerList.remove(item)
        GlobalScope.launch(Dispatchers.Main) {
            notifyItemRemoved(pos)
        }
    }

    fun addNewPlayer(playerName: String) {
        if (playerList.map { player -> player.playerName }.contains(playerName))
            return
        playerList.add(PlayerDomainModel(playerName, "", -1))
        notifyItemInserted(playerList.indexOf(playerList.find { p -> p.playerName == playerName }))
        listener.refreshSelection()
    }

    fun setCharacterTextColor(playerName: String, color: Int) {
        val item = playerList.find { player -> player.playerName == playerName }!!
        val payloads: MutableList<Int> = ArrayList()
        payloads.add(color)
        GlobalScope.launch(Dispatchers.Main) {
            notifyItemRangeChanged(playerList.indexOf(item), 1, payloads)
        }
    }
}