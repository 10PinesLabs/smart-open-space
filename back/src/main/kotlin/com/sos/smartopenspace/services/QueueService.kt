package com.sos.smartopenspace.services

import com.sos.smartopenspace.domain.OpenSpace
import com.sos.smartopenspace.domain.OpenSpaceNotFoundException
import com.sos.smartopenspace.dto.response.TalkResponseDTO
import com.sos.smartopenspace.persistence.OpenSpaceRepository
import com.sos.smartopenspace.translators.response.TalkResTranslator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QueueService(
    private val openSpaceRepository: OpenSpaceRepository,
) {

    @Transactional(readOnly = true)
    fun getQueueFromOpenSpaceId(openSpaceId: Long): List<TalkResponseDTO> =
        TalkResTranslator.translateAllFrom(findById(openSpaceId).queue.toList())

    @Transactional(readOnly = true)
    fun getQueueFromOpenSpace(os: OpenSpace): List<TalkResponseDTO> =
        TalkResTranslator.translateAllFrom(os.queue.toList())

    private fun findById(id: Long) = openSpaceRepository.findByIdOrNull(id) ?: throw OpenSpaceNotFoundException()
}