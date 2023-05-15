package solutions.mk.mobile.config

import android.content.ContentResolver
import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.ksp.generated.module
import solutions.mk.mobile.MainActivity
import solutions.mk.mobile.persist.ApplicationDatabase
import solutions.mk.mobile.persist.migrationChangelog

@Module
@ComponentScan("solutions.mk.mobile")
class KoinConfig(private val mainApplicationContext: MainActivity) {
    fun startKoin() = org.koin.core.context.startKoin {
        androidLogger()
        androidContext(mainApplicationContext)
        modules(
            module,
            androidModule,
            roomModule,
        )
    }

    private val androidModule = module {
        single { mainApplicationContext.contentResolver } binds arrayOf(ContentResolver::class)
    }

    private val roomModule = module {
        val database = createDataBase()
        single { database }
        single { database.recordRepo }
    }


    private fun createDataBase() = Room.databaseBuilder(
        mainApplicationContext,
        ApplicationDatabase::class.java,
        "mobile.mk.solutions"
    ).addMigrations(*migrationChangelog).build()

}