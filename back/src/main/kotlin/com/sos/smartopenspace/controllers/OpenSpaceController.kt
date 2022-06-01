package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.domain.OpenSpace
import com.sos.smartopenspace.helpers.CreateTalkDTO
import com.sos.smartopenspace.services.OpenSpaceService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("openSpace")
class OpenSpaceController(private val openSpaceService: OpenSpaceService) {
  @PostMapping("/{userID}")
  fun create(@PathVariable userID: Long, @Valid @RequestBody openSpace: OpenSpace) = openSpaceService.create(userID, openSpace)

  @PostMapping("/talk/{userID}/{osID}")
  fun createTalk(@PathVariable userID: Long, @PathVariable osID: Long, @Valid @RequestBody createTalkDTO: CreateTalkDTO) =
    openSpaceService.createTalk(userID, osID, createTalkDTO)

  @GetMapping("/user/{userID}")
  fun findAllByUser(@PathVariable userID: Long) = openSpaceService.findAllByUser(userID)

  @GetMapping("/{id}")
  fun findById(@PathVariable id: Long) = openSpaceService.findById(id)

  @GetMapping("/talks/{userID}/{osID}")
  fun findTalksByUser(@PathVariable userID: Long, @PathVariable osID: Long) = openSpaceService.findTalksByUser(userID, osID)

  @GetMapping("/talks/{id}")
  fun findTalks(@PathVariable id: Long) = openSpaceService.findTalks(id)

  @GetMapping("/assignedSlots/{id}")
  fun findAssignedSlotsById(@PathVariable id: Long) = openSpaceService.findAssignedSlotsById(id)

  @PutMapping("/activateQueue/{userID}/{osID}")
  fun activateQueue(@PathVariable userID: Long, @PathVariable osID: Long) =
    openSpaceService.activateQueue(userID, osID)

  @PutMapping("/finishQueue/{userID}/{osID}")
  fun finishQueue(@PathVariable userID: Long, @PathVariable osID: Long) =
    openSpaceService.finishQueue(userID, osID)

  @PutMapping("/enqueueTalk/{userID}/{talkID}")
  fun enqueueTalk(@PathVariable userID: Long, @PathVariable talkID: Long) =
    openSpaceService.enqueueTalk(userID, talkID)

  @PutMapping("/{openSpaceId}/user/{userID}/callForPapers")
  fun callForPapers(@PathVariable userID: Long, @PathVariable openSpaceId: Long) =
    openSpaceService.toggleCallForPapers(openSpaceId, userID)
}
