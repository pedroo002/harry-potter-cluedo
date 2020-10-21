package neptun.jxy1vz.cluedo.ui.fragment.character_selector.multi.adapter

import androidx.recyclerview.widget.DiffUtil
import neptun.jxy1vz.cluedo.network.model.PlayerApiModel

object PlayerItemComparator : DiffUtil.ItemCallback<PlayerApiModel>() {
    override fun areItemsTheSame(oldItem: PlayerApiModel, newItem: PlayerApiModel): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: PlayerApiModel, newItem: PlayerApiModel): Boolean {
        return oldItem == newItem
    }
}