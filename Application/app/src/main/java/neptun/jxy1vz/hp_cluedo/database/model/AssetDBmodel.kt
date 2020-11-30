package neptun.jxy1vz.hp_cluedo.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Assets")
data class AssetDBmodel(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "url")
    val url: String,
    @ColumnInfo(name = "tag")
    val tag: String
)

enum class AssetPrefixes {
    DARK_CARDS,
    HELPER_CARDS,
    MYSTERY_CARDS,
    PLAYER_CARDS,
    DARK_MARK,
    DARK_CARD_FRAGMENT,
    DICE,
    DOOR,
    FOOTPRINT,
    GATEWAY,
    NOTE,
    MAP,
    SELECTION,
    TILE,
    MENU,
    TUTORIAL,
    MYSTERY_ROOM_TOKENS,
    MYSTERY_TOOL_TOKENS,
    MYSTERY_SUSPECT_TOKENS,
    PLAYER_TOKENS
}

fun AssetPrefixes.string(): String {
    val base = "resources/"
    return when (this) {
        AssetPrefixes.DARK_CARDS -> "${base}cards/dark%"
        AssetPrefixes.HELPER_CARDS -> "${base}cards/helper%"
        AssetPrefixes.MYSTERY_CARDS -> "${base}cards/mystery%"
        AssetPrefixes.PLAYER_CARDS -> "${base}cards/player%"
        AssetPrefixes.DARK_MARK -> "${base}map/dark_mark%"
        AssetPrefixes.DARK_CARD_FRAGMENT -> "${base}map/dark_card%"
        AssetPrefixes.DICE -> "${base}map/dice%"
        AssetPrefixes.DOOR -> "${base}map/door%"
        AssetPrefixes.FOOTPRINT -> "${base}map/footprint%"
        AssetPrefixes.GATEWAY -> "${base}map/gateway%"
        AssetPrefixes.NOTE -> "${base}map/note%"
        AssetPrefixes.MAP -> "${base}map/other%"
        AssetPrefixes.SELECTION -> "${base}map/selection%"
        AssetPrefixes.TILE -> "${base}map/tile%"
        AssetPrefixes.MENU -> "${base}menu/other%"
        AssetPrefixes.TUTORIAL -> "${base}menu/tutorial%"
        AssetPrefixes.MYSTERY_ROOM_TOKENS -> "${base}tokens/mystery/room%"
        AssetPrefixes.MYSTERY_TOOL_TOKENS -> "${base}tokens/mystery/tool%"
        AssetPrefixes.MYSTERY_SUSPECT_TOKENS -> "${base}tokens/mystery/suspect%"
        AssetPrefixes.PLAYER_TOKENS -> "${base}tokens/player%"
    }
}