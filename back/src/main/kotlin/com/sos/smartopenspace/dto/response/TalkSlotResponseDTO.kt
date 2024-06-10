package com.sos.smartopenspace.dto.response

import java.time.LocalDate
import java.time.LocalTime

data class TalkSlotResponseDTO(
    val id: Long,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val date: LocalDate,
    val type: String,
)