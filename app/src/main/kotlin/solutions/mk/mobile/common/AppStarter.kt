package solutions.mk.mobile.common

import android.util.Log
import solutions.mk.mobile.MainActivity
import solutions.mk.mobile.config.KoinConfig

/**
 * Responsibilities and contract properties:
 * - create instance at MainActivity.onCreate() - after Android framework started.
 * - initLogic() - Must invoke only once at JVM Runtime! For avoid crush after second invoke method: startElseIgnore().
 * - encapsulate all application preparation things.
 */
val appStarter by lazy { AppStarter() }

class AppStarter {
    private lateinit var mainApplicationContext: MainActivity
    private var isStarted = false

    fun startElseIgnore(mainActivity: MainActivity) {
        if (isStarted) return
        Log.i(this.javaClass.simpleName, "# startElseIgnore() - RUN")
        this.mainApplicationContext = mainActivity
        initLogic()
        isStarted = true
    }

    /** Restriction: Must invoke only once at JVM Runtime! */
    private fun initLogic() {
        KoinConfig(mainApplicationContext).startKoin()
    }

}