package solutions.mk.mobile.config

import android.content.ContentResolver
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
//import org.koin.core.annotation.ComponentScan
//import org.koin.core.annotation.Module
import org.koin.dsl.binds
import org.koin.dsl.module
//import org.koin.ksp.generated.module
import solutions.mk.mobile.MainActivity
import solutions.mk.mobile.persist.createDataBase
import solutions.mk.mobile.service.PDFConverter
import solutions.mk.mobile.service.RecordFileService

/**
 */
//@Module
//@ComponentScan("solutions.mk.mobile")
class KoinConfig(private val mainApplicationContext: MainActivity) {
    fun startKoin() = org.koin.core.context.startKoin {
        androidLogger()
        androidContext(mainApplicationContext)
        modules(
            appModule,
            androidModule,
            roomModule,
        )
    }

    private val androidModule = module {
        single { mainApplicationContext.contentResolver } binds arrayOf(ContentResolver::class)
    }

    private val roomModule = module {
        with(createDataBase(mainApplicationContext)) {
            single { this }
            single { recordRepo }
            single { groupRepo }
            single { recordAndGroupRelationRepo }
        }
    }

    /**
     * For case if Koin ksp will not work
     */
    private val appModule = module {
        single { RecordFileService() } // this class broke Koin ksp  - why? IDK, it's fucking magic! ^_^
        single { ApplicationConfig() }
        single { PDFConverter() }
    }

}