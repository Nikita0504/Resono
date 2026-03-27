package com.dev.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dev.database.entity.AudioAlbumEntity
import com.dev.database.entity.AudioAlbumTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioAlbumsDao {

    @Query("SELECT * FROM audio_albums ORDER BY title ASC")
    fun observeAlbums(): Flow<List<AudioAlbumEntity>>

    @Query("SELECT * FROM audio_albums WHERE album_id = :albumId LIMIT 1")
    fun observeAlbum(albumId: String): Flow<AudioAlbumEntity?>

    @Query("SELECT * FROM audio_album_tracks")
    fun observeAlbumTracks(): Flow<List<AudioAlbumTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAlbum(entity: AudioAlbumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumTracks(entities: List<AudioAlbumTrackEntity>)

    @Query("DELETE FROM audio_album_tracks WHERE album_id = :albumId")
    suspend fun deleteAlbumTracks(albumId: String)

    @Query("DELETE FROM audio_albums WHERE album_id = :albumId")
    suspend fun deleteAlbum(albumId: String)

    @Transaction
    suspend fun replaceAlbumTracks(albumId: String, audioIds: List<String>) {
        deleteAlbumTracks(albumId)
        if (audioIds.isNotEmpty()) {
            insertAlbumTracks(
                audioIds.distinct().map { audioId ->
                    AudioAlbumTrackEntity(albumId = albumId, audioId = audioId)
                },
            )
        }
    }
}
