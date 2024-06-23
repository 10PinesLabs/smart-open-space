package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.Review
import com.sos.smartopenspace.dto.response.ReviewResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object ReviewResTranslator : TranslatorFrom<Review, ReviewResponseDTO> {
    override fun translateFrom(domain: Review) = ReviewResponseDTO(
        id = domain.id,
        grade = domain.grade,
        comment = domain.comment,
        reviewer = UserResTranslator.translateFrom(domain.reviewer),
    )
}