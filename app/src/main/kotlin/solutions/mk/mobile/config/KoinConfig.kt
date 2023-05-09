package solutions.mk.mobile.config

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.ksp.generated.module
import solutions.mk.mobile.MainActivity

@Module
@ComponentScan("solutions.mk.mobile")
class KoinConfig {
    companion object {
        fun startKoin (mainApplicationContext : MainActivity) = org.koin.core.context.startKoin {
            androidLogger()
            androidContext(mainApplicationContext)
            modules(KoinConfig().module)
        }
    }

//    @Single fun taskConverter(): TaskConverter = Mappers.getMapper(TaskConverter::class.java)
}