package com.sos.smartopenspace.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import com.sos.smartopenspace.domain.OpenSpace
import com.sos.smartopenspace.dto.response.AssignedSlotResponseDTO
import com.sos.smartopenspace.services.OpenSpaceService
import com.sos.smartopenspace.translators.AssignedSlotResTranslator
import org.springframework.stereotype.Component

@Component
class ScheduleSocket(
    private val openSpaceService: OpenSpaceService,
    objectMapper: ObjectMapper
) : AbstractSocket<List<AssignedSlotResponseDTO>>(objectMapper) {
    override fun getData(id: Long) = openSpaceService.findAssignedSlotsById(id)
    override fun getData(os: OpenSpace) = AssignedSlotResTranslator.translateAllFrom(os.assignedSlots.toList())

}