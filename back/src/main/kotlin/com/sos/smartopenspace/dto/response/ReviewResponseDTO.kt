package com.sos.smartopenspace.dto.response

data class ReviewResponseDTO(
    val id: Long,
    val grade: Int,
    val comment: String?,
    val reviewer: UserResponseDTO,
)