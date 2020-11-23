package neptun.jxy1vz.hp_cluedo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import neptun.jxy1vz.hp_cluedo.database.dao.CardDAO
import neptun.jxy1vz.hp_cluedo.database.dao.DarkHelperDAO
import neptun.jxy1vz.hp_cluedo.database.dao.NoteDAO
import neptun.jxy1vz.hp_cluedo.database.dao.PlayerDAO
import neptun.jxy1vz.hp_cluedo.database.model.CardDBmodel
import neptun.jxy1vz.hp_cluedo.database.model.DarkHelperPairDBmodel
import neptun.jxy1vz.hp_cluedo.database.model.NoteDBmodel
import neptun.jxy1vz.hp_cluedo.database.model.PlayerDBmodel

@Database(entities = [CardDBmodel::class, DarkHelperPairDBmodel::class, NoteDBmodel::class, PlayerDBmodel::class], version = 1)
abstract class CluedoDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
    abstract fun darkHelperDao(): DarkHelperDAO
    abstract fun noteDao(): NoteDAO
    abstract fun playerDao(): PlayerDAO

    companion object {
        private lateinit var db: CluedoDatabase

        fun getInstance(context: Context): CluedoDatabase {
            if (!this::db.isInitialized) {
                db = Room.databaseBuilder(
                    context.applicationContext,
                    CluedoDatabase::class.java,
                    "cluedo-database"
                ).build()
            }
            return db
        }
    }
}