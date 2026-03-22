package com.dev.logger

enum class LogLevel : Comparable<LogLevel> {
    VERBOSE,
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    CRASH;
}