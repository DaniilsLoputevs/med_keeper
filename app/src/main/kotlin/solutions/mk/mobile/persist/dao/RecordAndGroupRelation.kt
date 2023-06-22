package solutions.mk.mobile.persist.dao

import androidx.room.*

@Entity(
    tableName = "record__group__relation",
    primaryKeys = ["file_name", "group_name"]
)
data class RecordAndGroupRelation(
    @ColumnInfo("file_name")
    val fileName: String,
    @ColumnInfo("group_name")
    val groupName: String,
)

@Dao interface RecordAndGroupRelationRepo : CrudRepo<RecordAndGroupRelation> {

    @Query("SELECT * FROM record__group__relation")
    suspend fun getAll(): List<RecordAndGroupRelation>
}