package com.dev.domain.model

data class WaveformData(
    val mediaId: String,
    val samples: List<Float>,
    val source: Source,
    val sampleRateHz: Int? = null,
) {
    enum class Source {
        LIVE,
        PRECOMPUTED,
    }
}
