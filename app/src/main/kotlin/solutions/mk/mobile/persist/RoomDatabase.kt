package solutions.mk.mobile.persist

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import solutions.mk.mobile.common.inject
import solutions.mk.mobile.persist.dao.RecordEntity
import solutions.mk.mobile.persist.dao.RecordRepo
import solutions.mk.mobile.persist.migrations.MIGRATION__INIT

/**
 * Migration changelog - list of all actual migrations.
 * Element order is IMPORTANT!!!
 */
val migrationChangelog = arrayOf(
    MIGRATION__INIT,
)

/**
 *  Main Room class - provide access to all DAO object which created at run-time by Room Framework.
 *  DAO object create from all interfaces with annotation [@Dao][androidx.room.Dao].
 *  Note: Class implementation of DAO interface will be created at compile-time.
 *
 *  How to add new DAO interface(repository):
 *  Declare val property with type DAO Interface
 *  AND
 *  add new DAO interface(repository) in [KoinConfig.roomModule][solutions.mk.mobile.config.KoinConfig.roomModule].
 *  ```
 *  single { database.recordRepo }
 *  ```
 *
 *  How to add new Entity(table projection):
 *  add Entity class to annotation parameter {Database(entities = [...])}
 *  ```
 *  @Database(
 *     entities = [RecordEntity::class],
 *     version = 1
 * )
 *  ```
 *
 *  How to receive repository from Koin:
 *  ```
 *  private val recordRepo: RecordRepo by inject()
 *  ```
 *
 *
 *  How to use repository:
 *  ```
 *  sql {
 *      recordRepo.insertAll(RecordEntity(recordFile.name, "stub-desc", "stub-issuer"))
 *  }
 *  ```
 *  Note: use MUST invoke sql queries from NON-UI thread!!! That why we use function "sql {}"
 */
@Database(
    entities = [RecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract val recordRepo: RecordRepo
}

/**
 * IMPORTANT!!!
 * 1 - We MUST use raw table in migration sql - avoid DAO interface!!! This is restriction from Room framework.
 * 2 - We MUST add new migration into [migrationChangelog][migrationChangelog] else magic will not happen.
 */
fun migration(startVersion: Int, endVersion: Int, block: SupportSQLiteDatabase.() -> Unit) =
    object : Migration(startVersion, endVersion) {
        override fun migrate(database: SupportSQLiteDatabase) = database.block()
    }

/* This is extension only for highlight call of this function. */
/**
 * Run sql in Non-Main-thread thread but Blocking and waiting of result.
 * */
fun <T> Any.sql(useRepositoryBlock: suspend () -> T): T = runBlocking { useRepositoryBlock() }
fun <T> Any.sqlTransaction(useRepositoryBlock: suspend () -> T): T = runBlocking {
    inject<ApplicationDatabase>().sqlTransaction { useRepositoryBlock() }
}

