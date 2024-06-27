package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.Track
import com.sos.smartopenspace.dto.response.TrackResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object TrackResTranslator : TranslatorFrom<Track, TrackResponseDTO> {
    override fun translateFrom(domain: Track) = TrackResponseDTO(
        id = domain.id,
        name = domain.name,
        color = domain.color,
        description = domain.description,
    )
}