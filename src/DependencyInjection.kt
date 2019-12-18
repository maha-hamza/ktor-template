import com.uchuhimo.konf.Config
import org.koin.Logger.slf4jLogger
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin(config: Config) {
    startKoin {
        modules(modules(config))
        slf4jLogger()
    }
}

fun modules(config: Config, overrides: List<Module> = emptyList()): List<Module> {
    return listOf(
        module {
            single { config }
            single { TestController() }

        }
    ) + overrides
}

inline fun <reified T : Any> inject(): T {
    return GlobalContext.get().koin.get()
}

inline fun <reified T : Any> lazyInject(): Lazy<T> {
    return lazy { inject<T>() }
}