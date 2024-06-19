package com.sos.smartopenspace.dto.response

import com.sos.smartopenspace.dto.SimpleIdDTO

data class TalkResponseDTO(
    val id: Long,
    val name: String,
    val description: String,
    val meetingLink: String?,
    val track: TrackResponseDTO?,
    val speaker: SpeakerResponseDTO,
    val documents: List<SimpleIdDTO>,
)