package neptun.jxy1vz.cluedo.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class CluedoDatabase : RoomDatabase() {

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