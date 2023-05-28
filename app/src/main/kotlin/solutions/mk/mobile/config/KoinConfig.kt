package solutions.mk.mobile.config

import android.content.ContentResolver
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.ksp.generated.module
import solutions.mk.mobile.MainActivity
import solutions.mk.mobile.persist.createDataBase

/**
 * TODO - impl bean with all i18n string resources(simple access into code for all string resources by 1 access point)
 */
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
        val database = createDataBase(mainApplicationContext)
        single { database }
        single { database.recordRepo }
        single { database.groupRepo }
        single { database.recordAndGroupRelationRepo }
    }

}