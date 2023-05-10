package solutions.mk.mobile.common

import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

/* Koin */
/**
 * Retrieve given dependency for KoinComponent
 * @param qualifier - bean name / optional
 * @param parameters
 */
inline fun <reified T : Any> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = GlobalContext.get().get<T>(qualifier, parameters)

/**
 * Just marker method - this object got from Android framework context,
 * not form our code context.
 * Technically equal to get()
 */
inline fun <reified T : Any> getAndroid(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = get<T>(qualifier, parameters)


/**
 * inject lazily given dependency
 * @param qualifier - bean name / optional
 * @param parameters
 */
inline fun <reified T : Any> inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = lazy { GlobalContext.get().get<T>(qualifier, parameters) }


/**
 * Just marker method - this object injected from Android framework context,
 * not form our code context.
 * Technically equal to inject()
 */
inline fun <reified T : Any> injectAndroid(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
) = inject<T>(qualifier, parameters)

