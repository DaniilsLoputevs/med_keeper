package solutions.mk.mobile.persist.dao

import androidx.room.*


/**
-- fileName - имя файла + расширение
-- description
-- ? issuer // кем-чем выдан документ - подсказки-подстановка
-- ? issueDateTime  || issueDate -  когда выдан документ
-- ? uploadDateTime - время загрузки
-- tags/groups/packages/categories : List<String> - магия группировки


--     issue_date_time_utc0 TEXT NOT NULL, -- format: 2023-12-31 00:00 UTC-0
--     upload_date_time_utc0 TEXT NOT NULL, -- format: 2023-12-31 00:00 UTC-0
 */
@Entity(tableName = "records")
data class RecordEntity(
    @ColumnInfo("file_name")
    @PrimaryKey val fileName: String,

    @ColumnInfo(defaultValue = "")
    val description: String,
)

@Dao
interface RecordRepo {

    @Query("SELECT * FROM records")
    suspend fun getAll(): List<RecordEntity>

    @Insert suspend fun insertAll(vararg records: RecordEntity)
    @Update suspend fun updateAll(vararg records: RecordEntity)
    @Delete suspend fun deleteAll(vararg records: RecordEntity)

    @Query("SELECT exists(SELECT 1 FROM records WHERE file_name = :filename)")
    suspend fun containsByName(filename: String): Boolean
}