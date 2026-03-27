package com.dev.database.db

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dev.database.dao.AudioOverridesDao
import com.dev.database.dao.AudioAlbumsDao
import com.dev.database.entity.AudioAlbumEntity
import com.dev.database.entity.AudioAlbumTrackEntity
import com.dev.database.entity.AudioOverrideEntity

@Database(
    entities = [AudioOverrideEntity::class, AudioAlbumEntity::class, AudioAlbumTrackEntity::class],
    version = 3,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun audioOverridesDao(): AudioOverridesDao

    abstract fun audioAlbumsDao(): AudioAlbumsDao

    companion object {
        const val DATABASE_NAME: String = "resono.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE audio_overrides ADD COLUMN custom_artist TEXT")
                db.execSQL("ALTER TABLE audio_overrides ADD COLUMN custom_album TEXT")
                db.execSQL("ALTER TABLE audio_overrides ADD COLUMN custom_album_art_uri TEXT")
                db.execSQL("ALTER TABLE audio_overrides ADD COLUMN custom_track_number INTEGER")
                db.execSQL("ALTER TABLE audio_overrides ADD COLUMN custom_year INTEGER")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `audio_albums` (
                        `album_id` TEXT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT,
                        `artwork_uri` TEXT,
                        PRIMARY KEY(`album_id`)
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `audio_album_tracks` (
                        `album_id` TEXT NOT NULL,
                        `audio_id` TEXT NOT NULL,
                        PRIMARY KEY(`album_id`, `audio_id`),
                        FOREIGN KEY(`album_id`) REFERENCES `audio_albums`(`album_id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_audio_album_tracks_album_id` ON `audio_album_tracks` (`album_id`)",
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_audio_album_tracks_audio_id` ON `audio_album_tracks` (`audio_id`)",
                )
            }
        }
    }
}
