package com.dev.logger

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val loggerModule = module {

    singleOf<Logger>(::AppLoggerImpl)

}