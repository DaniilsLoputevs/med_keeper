package solutions.mk.mobile.config

import android.content.ContentResolver
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.binds
import org.koin.dsl.module
import org.koin.ksp.generated.module
import solutions.mk.mobile.MainActivity

@Module
@ComponentScan("solutions.mk.mobile")
class KoinConfig {
    companion object {
        fun startKoin(mainApplicationContext: MainActivity) = org.koin.core.context.startKoin {
            androidLogger()
            androidContext(mainApplicationContext)
            modules(KoinConfig().module)
            loadBeans {
                single { mainApplicationContext.contentResolver } binds arrayOf(ContentResolver::class)
            }
        }

        private fun KoinApplication.loadBeans(block: org.koin.core.module.Module.() -> Unit) =
            koin.loadModules(listOf(module {
                block(this@module)
            }))

    }

}