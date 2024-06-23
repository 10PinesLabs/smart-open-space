package com.sos.smartopenspace.dto.response

import com.sos.smartopenspace.domain.QueueState
import java.time.LocalDate
import java.time.LocalTime


data class OpenSpaceResponseDTO(
    val id: Long,
    val name: String,
    val description: String,
    val urlImage: String,
    val queueState: QueueState,
    val isActiveCallForPapers: Boolean,
    val pendingQueue: Boolean,
    val activeQueue: Boolean,
    val finishedQueue: Boolean,
    val dates: Set<LocalDate>,
    val startTime: LocalTime?,
    val endTime: LocalTime?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val amountOfTalks: Int,
    val organizer: UserResponseDTO,
    val rooms: List<RoomResponseDTO>,
    val slots: List<SlotResponseDTO>,
    val tracks: List<TrackResponseDTO>,
    val toSchedule: List<TalkResponseDTO>,
    val freeSlots: List<Pair<RoomResponseDTO, List<SlotResponseDTO>>>,
    val assignableSlots: List<Pair<RoomResponseDTO, List<SlotResponseDTO>>>,
)