package com.sos.smartopenspace.dto.response

import com.sos.smartopenspace.dto.SimpleIdDTO

data class AssignedSlotResponseDTO(
    val id: Long,
    val slot: TalkSlotResponseDTO,
    val room: SimpleIdDTO,
    val talk: TalkResponseDTO,
)