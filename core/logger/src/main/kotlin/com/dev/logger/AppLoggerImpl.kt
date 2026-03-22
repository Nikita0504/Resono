package com.dev.logger

import android.util.Log
import java.util.concurrent.ConcurrentLinkedDeque
import com.dev.logger.BuildConfig


class AppLoggerImpl(
    private val maxCacheSize: Int = 1000,
    //private val crashReporter: CrashReporter? = null,
) : Logger {

    private val logCache = ConcurrentLinkedDeque<LogEntry>()

    override fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        val entry = LogEntry(level, tag, message, throwable)

        logToLogcat(entry)
        cacheLog(entry)

        if (level >= LogLevel.ERROR) {
            //crashReporter?.report(entry)
        }
    }

    private fun cacheLog(entry: LogEntry) {
        // Атомарное вытеснение старых записей
        while (logCache.size > maxCacheSize) {
            logCache.pollFirst()
        }
    }

    private fun logToLogcat(entry: LogEntry) {
        if (!BuildConfig.DEBUG) return
        val msg = entry.message + (entry.throwable?.let { "\n${it.stackTraceToString()}" } ?: "")
        when (entry.level) {
            LogLevel.VERBOSE -> Log.v(entry.tag, msg)
            LogLevel.DEBUG   -> Log.d(entry.tag, msg)
            LogLevel.INFO    -> Log.i(entry.tag, msg)
            LogLevel.WARNING -> Log.w(entry.tag, msg)
            LogLevel.ERROR,
            LogLevel.CRASH   -> Log.e(entry.tag, msg)
        }
    }

    override fun getLogs(): List<LogEntry> = logCache.toList()
    override fun clearLogs() = logCache.clear()
}
