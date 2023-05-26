package solutions.mk.mobile.persist.dao

import androidx.room.*

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val name: String
)

@Dao interface GroupRepo {

    @Query("SELECT * FROM groups")
    suspend fun getAll(): List<GroupEntity>
    @Insert suspend fun insert(records: GroupEntity)
    @Insert suspend fun insertAll(records: Iterable<GroupEntity>)
    @Update suspend fun updateAll(vararg records: GroupEntity)
    @Delete suspend fun deleteAll(vararg records: GroupEntity)
}