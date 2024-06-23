package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.Talk
import com.sos.smartopenspace.dto.response.TalkResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object TalkResTranslator : TranslatorFrom<Talk, TalkResponseDTO> {
    override fun translateFrom(domain: Talk) = TalkResponseDTO(
        id = domain.id,
        name = domain.name,
        description = domain.description,
        meetingLink = domain.meetingLink?.toString(),
        track = domain.track?.let { TrackResTranslator.translateFrom(it) },
        speaker = UserResTranslator.translateFrom(domain.speaker),
        documents = DocumentResTranslator.translateAllFrom(domain.documents.toList()),
        reviews = ReviewResTranslator.translateAllFrom(domain.reviews.toList()),
        votingUsers = UserResTranslator.translateAllFrom(domain.votingUsers.toList()),
        votes = domain.votes(),
    )
}