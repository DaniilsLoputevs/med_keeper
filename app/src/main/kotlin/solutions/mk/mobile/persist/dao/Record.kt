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
    /**
     * Store full filename with extension and without path. Example: "test_pdf_file.pdf"
     */
    @ColumnInfo("file_name")
    @PrimaryKey val fileName: String,

    @ColumnInfo(defaultValue = "")
    val description: String,
)

val RecordEntity.fileExtension: String get() = this.fileName.split(".").last()

@Dao interface RecordRepo : CrudRepo<RecordEntity> {

    @Query("SELECT * FROM records r WHERE r.file_name IN (SELECT rgl.file_name FROM record__group__relation rgl WHERE rgl.group_name = :groupName)")
    suspend fun getAllByGroupName(groupName: String): List<RecordEntity>

    @Query("SELECT * FROM records")
    suspend fun getAll(): List<RecordEntity>

    @Query("SELECT exists(SELECT 1 FROM records WHERE file_name = :filename)")
    suspend fun containsByName(filename: String): Boolean
}