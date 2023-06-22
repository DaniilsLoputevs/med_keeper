package solutions.mk.mobile.persist.dao

import androidx.room.*

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val name: String
)

@Dao interface GroupRepo : CrudRepo<GroupEntity> {

    @Query("SELECT name FROM groups")
    suspend fun getAllNames(): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfNotExists(records: GroupEntity)

    @Query("SELECT * FROM groups")
    suspend fun getAll(): List<GroupEntity>

}