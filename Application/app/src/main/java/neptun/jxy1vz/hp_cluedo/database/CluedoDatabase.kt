package neptun.jxy1vz.hp_cluedo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import neptun.jxy1vz.hp_cluedo.database.dao.*
import neptun.jxy1vz.hp_cluedo.database.model.*

@Database(entities = [CardDBmodel::class, DarkHelperPairDBmodel::class, NoteDBmodel::class, PlayerDBmodel::class, AssetDBmodel::class], version = 1)
abstract class CluedoDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDAO
    abstract fun darkHelperDao(): DarkHelperDAO
    abstract fun noteDao(): NoteDAO
    abstract fun playerDao(): PlayerDAO
    abstract fun assetDao(): AssetDAO

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