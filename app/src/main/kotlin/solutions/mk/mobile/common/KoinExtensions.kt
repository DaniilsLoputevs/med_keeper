package solutions.mk.mobile.common

import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/**
 * Eager injection KoinComponent.
 * [org.koin.android.ext.android.get] - koin extension for Android buy available only inside child of Android classes.
 * @param qualifier - bean name / optional.
 * @param parameters
 */
inline fun <reified T : Any> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = GlobalContext.get().get<T>(qualifier, parameters)

/**
 * Just marker method - this object got from Android framework context, not form our code context.
 * Technically equal to [solutions.mk.mobile.common.get].
 */
inline fun <reified T : Any> getAndroid(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = get<T>(qualifier, parameters)


/**
 * Lazily injection KoinComponent.
 * [org.koin.android.ext.android.inject] - koin extension for Android buy available only inside child of Android classes.
 * @param qualifier - bean name / optional.
 * @param parameters
 */
inline fun <reified T : Any> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { GlobalContext.get().get<T>(qualifier, parameters) }


/**
 * Just marker method - this object injected from Android framework context, not form our code context.
 * Technically equal to [solutions.mk.mobile.common.inject].
 */
inline fun <reified T : Any> injectAndroid(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = inject<T>(qualifier, parameters)

