package solutions.mk.mobile.persist

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.*
import solutions.mk.mobile.MainActivity
import solutions.mk.mobile.persist.dao.*
import solutions.mk.mobile.persist.migrations.migrationChangelog

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
 *  sqlBlocking {
 *      recordRepo.insertAll(RecordEntity(recordFile.name, "stub-desc", "stub-issuer"))
 *  }
 *  ```
 *  Note: use MUST invoke sql queries from NON-UI thread!!! That why we use function "sql {}"
 */
@Database(
    entities = [
        RecordEntity::class,
        GroupEntity::class,
        RecordAndGroupRelation::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract val recordRepo: RecordRepo
    abstract val groupRepo: GroupRepo
    abstract val recordAndGroupRelationRepo: RecordAndGroupRelationRepo
}

/**
 * IMPORTANT!!!
 * 1 - We MUST use raw table in migration sql - avoid DAO interface!!! This is restriction from Room framework.
 * 2 - We MUST add new migration into [migrationChangelog][migrationChangelog] else magic will not happen.
 * 3 - We MUST update the version of database in code.
 * 4 - Each migration once executed should be not edit anymore.
 */
fun migration(startVersion: Int, endVersion: Int, block: SupportSQLiteDatabaseMigrationDecorator.() -> Unit) =
    object : Migration(startVersion, endVersion) {
        override fun migrate(database: SupportSQLiteDatabase) =
            SupportSQLiteDatabaseMigrationDecorator(database).block()
    }

/**
 * exists for add option for write migration like this:
 * ```
 * +"""
 *     CREATE TABLE IF NOT EXISTS `groups`
 *     (
 *         `name` TEXT NOT NULL,
 *         PRIMARY KEY (`name`)
 *     );
 *     """.trimIndent()
 * ```
 * ```
 * execSQL(
 *     """
 *     CREATE TABLE IF NOT EXISTS `groups`
 *     (
 *         `name` TEXT NOT NULL,
 *         PRIMARY KEY (`name`)
 *     );
 * """.trimIndent()
 * )
 * ```
 */
class SupportSQLiteDatabaseMigrationDecorator(private val original: SupportSQLiteDatabase) :
    SupportSQLiteDatabase by original {
    operator fun String.unaryPlus() = original.execSQL(this)
}


val sqlCoroutineScope = CoroutineScope(Dispatchers.IO)

/**
 * Run sql in Non-Main-thread thread but Blocking and waiting of result.
 * */
fun <T> sqlBlocking(useRepositoryBlock: suspend () -> T): T = runBlocking { useRepositoryBlock() }
fun <T> sqlAsync(useRepositoryBlock: suspend () -> T): Deferred<T> = sqlCoroutineScope.async { useRepositoryBlock() }


fun createDataBase(mainApplicationContext : MainActivity) = Room.databaseBuilder(
    mainApplicationContext,
    ApplicationDatabase::class.java,
    "mobile.mk.solutions"
).addMigrations(*migrationChangelog).let {
    try {
        it.build()
    } catch (e: Exception) {
        e.printStackTrace()
        /* Sometimes in case of an error at the start of the application, the stacktrace is
        not shown or comes with a long delay in the log from IDEA run. This Thread.sleep(...) is here to
        increase the chance of showing the stacktrace in the developer log.*/
        Thread.sleep(500)
        throw e
    }
}