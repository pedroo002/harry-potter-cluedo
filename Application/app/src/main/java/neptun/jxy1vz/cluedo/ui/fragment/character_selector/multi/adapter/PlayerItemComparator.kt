package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.adapter

import androidx.recyclerview.widget.DiffUtil
import neptun.jxy1vz.cluedo.network.model.PlayerDomainModel

object PlayerItemComparator : DiffUtil.ItemCallback<PlayerDomainModel>() {
    override fun areItemsTheSame(oldItem: PlayerDomainModel, newItem: PlayerDomainModel): Boolean {
        return oldItem.playerName == newItem.playerName
    }

    override fun areContentsTheSame(oldItem: PlayerDomainModel, newItem: PlayerDomainModel): Boolean {
        return oldItem == newItem
    }
}