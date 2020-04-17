package neptun.jxy1vz.cluedo.database.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDAO<T> {
    @Insert
    suspend fun insertIntoTable(t: T): Long

    @Insert
    suspend fun insertIntoTable(list: List<T>?)

    @Update
    suspend fun updateTable(t: T)

    @Delete
    suspend fun deleteItem(t: T)
}