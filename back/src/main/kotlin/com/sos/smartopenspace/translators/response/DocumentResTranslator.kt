package com.sos.smartopenspace.translators.response

import com.sos.smartopenspace.domain.Document
import com.sos.smartopenspace.dto.response.DocumentResponseDTO
import com.sos.smartopenspace.translators.TranslatorFrom

object DocumentResTranslator : TranslatorFrom<Document, DocumentResponseDTO> {
    override fun translateFrom(domain: Document) = DocumentResponseDTO(
        id = domain.id,
        name = domain.name,
        link = domain.link,
    )

}