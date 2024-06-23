package com.sos.smartopenspace.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import com.sos.smartopenspace.domain.OpenSpace
import com.sos.smartopenspace.dto.response.TalkResponseDTO
import com.sos.smartopenspace.services.QueueService
import org.springframework.stereotype.Component

@Component
class QueueSocket(
    private val queueService: QueueService,
    objectMapper: ObjectMapper,
) : AbstractSocket<List<TalkResponseDTO>>(objectMapper) {
    override fun getData(id: Long) = queueService.getQueueFromOpenSpaceId(id)
    override fun getData(os: OpenSpace) =  queueService.getQueueFromOpenSpace(os)
}