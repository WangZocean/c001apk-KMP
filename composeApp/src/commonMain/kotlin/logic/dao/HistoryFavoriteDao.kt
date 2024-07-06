package logic.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import logic.model.FeedEntity


@Dao
interface HistoryFavoriteDao {

    @Insert
    suspend fun insert(data: FeedEntity)

    @Query("SELECT * FROM FeedEntity ORDER BY time DESC")
    suspend fun loadAllList(): List<FeedEntity>

    @Query("SELECT * FROM FeedEntity ORDER BY time DESC")
    fun loadAllListFlow(): Flow<List<FeedEntity>>

    @Query("SELECT 1 FROM FeedEntity WHERE id = :id LIMIT 1")
    suspend fun isExist(id: String): Boolean

    @Query("DELETE FROM FeedEntity WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM FeedEntity")
    suspend fun deleteAll()

    @Query("DELETE FROM FeedEntity WHERE uid = :uid")
    suspend fun deleteByUid(uid:String)

}