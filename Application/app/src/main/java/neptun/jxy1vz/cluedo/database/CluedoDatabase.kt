package neptun.jxy1vz.cluedo.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import neptun.jxy1vz.cluedo.database.dao.CardDAO
import neptun.jxy1vz.cluedo.database.dao.DarkHelperDAO
import neptun.jxy1vz.cluedo.database.dao.PlayerDAO

abstract class CluedoDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
    abstract fun darkHelperDao(): DarkHelperDAO
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