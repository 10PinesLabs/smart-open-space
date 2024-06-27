package com.sos.smartopenspace.dto.response

data class AssignedSlotResponseDTO(
    val id: Long,
    val slot: TalkSlotResponseDTO,
    val room: RoomResponseDTO,
    val talk: TalkResponseDTO,
)