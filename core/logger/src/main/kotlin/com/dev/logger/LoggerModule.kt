package com.dev.logger

import org.koin.dsl.module

val loggerModule = module {
    single<Logger> { AppLoggerImpl() }
}
