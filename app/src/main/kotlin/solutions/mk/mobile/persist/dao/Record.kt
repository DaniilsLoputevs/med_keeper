package solutions.mk.mobile.persist.dao

import androidx.room.*

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey val fileName: String,

    @ColumnInfo(defaultValue = "")
    val description: String,
    @ColumnInfo(defaultValue = "")
    val issuer: String, // todo - maybe impl as Table and Ref?
)

@Dao
interface RecordRepo {

    @Query("SELECT * FROM records")
    suspend fun getAll(): List<RecordEntity>

    @Insert suspend fun insertAll(vararg records: RecordEntity)
    @Update suspend fun updateAll(vararg records: RecordEntity)
    @Delete suspend fun deleteAll(vararg records: RecordEntity)
}