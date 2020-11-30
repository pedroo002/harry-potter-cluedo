package neptun.jxy1vz.hp_cluedo.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import neptun.jxy1vz.hp_cluedo.data.database.model.NoteDBmodel

@Dao
interface NoteDAO : BaseDAO<NoteDBmodel> {
    @Query("SELECT * FROM notes")
    suspend fun getNotes(): List<NoteDBmodel>?

    @Query("DELETE FROM notes")
    suspend fun eraseNotes()
}