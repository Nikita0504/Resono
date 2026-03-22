package com.dev.logger

interface Logger {
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable? = null)
    fun getLogs(): List<LogEntry>
    fun clearLogs()
}

fun Logger.v(tag: String, msg: String) = log(LogLevel.VERBOSE, tag, msg)
fun Logger.d(tag: String, msg: String) = log(LogLevel.DEBUG, tag, msg)
fun Logger.i(tag: String, msg: String) = log(LogLevel.INFO, tag, msg)
fun Logger.w(tag: String, msg: String, t: Throwable? = null) = log(LogLevel.WARNING, tag, msg, t)
fun Logger.e(tag: String, msg: String, t: Throwable) = log(LogLevel.ERROR, tag, msg, t)
fun Logger.crash(tag: String, msg: String, t: Throwable) = log(LogLevel.CRASH, tag, msg, t)