package neptun.jxy1vz.cluedo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import neptun.jxy1vz.cluedo.database.dao.CardDAO
import neptun.jxy1vz.cluedo.database.dao.DarkHelperDAO
import neptun.jxy1vz.cluedo.database.dao.NoteDAO
import neptun.jxy1vz.cluedo.database.model.CardDBmodel
import neptun.jxy1vz.cluedo.database.model.DarkHelperPairDBmodel
import neptun.jxy1vz.cluedo.database.model.NoteDBmodel

@Database(entities = [CardDBmodel::class, DarkHelperPairDBmodel::class, NoteDBmodel::class], version = 1)
abstract class CluedoDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
    abstract fun darkHelperDao(): DarkHelperDAO
    abstract fun noteDao(): NoteDAO

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