package com.sos.smartopenspace.websockets

import com.sos.smartopenspace.domain.Assignment
import com.sos.smartopenspace.domain.OpenSpace
import com.sos.smartopenspace.services.OpenSpaceService
import org.springframework.stereotype.Component

@Component
class ScheduleSocket(private val openSpaceService: OpenSpaceService) : AbstractSocket<List<Assignment>>() {
  override fun getData(id: Long) = openSpaceService.findAssignedSlotsById(id)
  override fun getData(os: OpenSpace) = os.assignments.toList()
}
