package com.sos.smartopenspace.dto.response

import java.net.URL

data class RoomResponseDTO(
    val id: Long,
    val name: String,
    val description: String,
    val link: URL?,
)