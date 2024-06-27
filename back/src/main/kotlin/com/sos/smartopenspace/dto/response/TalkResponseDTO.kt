package com.sos.smartopenspace.dto.response

data class TalkResponseDTO(
    val id: Long,
    val name: String,
    val description: String,
    val meetingLink: String?,
    val track: TrackResponseDTO?,
    val speaker: UserResponseDTO,
    val documents: List<DocumentResponseDTO>,
    val reviews: List<ReviewResponseDTO>,
    val votingUsers: List<UserResponseDTO>,
    val votes: Int,
)