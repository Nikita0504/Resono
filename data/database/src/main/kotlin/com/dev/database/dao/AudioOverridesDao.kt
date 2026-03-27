package com.dev.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.database.entity.AudioOverrideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioOverridesDao {

    @Query("SELECT * FROM audio_overrides")
    fun observeAll(): Flow<List<AudioOverrideEntity>>

    @Query("SELECT * FROM audio_overrides WHERE audio_id = :audioId LIMIT 1")
    suspend fun getByAudioId(audioId: String): AudioOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: AudioOverrideEntity)

    @Query("DELETE FROM audio_overrides WHERE audio_id = :audioId")
    suspend fun deleteByAudioId(audioId: String)
}
