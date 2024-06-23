package com.sos.smartopenspace.dto.response

import java.net.URL

data class DocumentResponseDTO(
    val id: Long,
    val link: URL,
    var name: String,
)