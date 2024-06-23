package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.dto.request.CreateTalkRequestDTO
import com.sos.smartopenspace.dto.request.OpenSpaceRequestDTO
import com.sos.smartopenspace.services.OpenSpaceService
import com.sos.smartopenspace.translators.response.OpenSpaceResTranslator
import com.sos.smartopenspace.translators.response.TalkResTranslator
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("openSpace")
class OpenSpaceController(private val openSpaceService: OpenSpaceService) {
  @PostMapping("/{userID}")
  fun create(@PathVariable userID: Long, @Valid @RequestBody openSpace: OpenSpaceRequestDTO) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.create(userID, openSpace))

  @PutMapping("/{openSpaceID}/user/{userID}")
  fun updateOpenSpace(@PathVariable openSpaceID: Long, @PathVariable userID: Long, @Valid @RequestBody openSpace: OpenSpaceRequestDTO) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.update(userID, openSpaceID, openSpace))

  @DeleteMapping("/{openSpaceID}/user/{userID}")
  fun delete(@PathVariable userID: Long, @PathVariable openSpaceID: Long) =
      openSpaceService.delete(userID, openSpaceID)

  @PostMapping("/talk/{userID}/{osID}")
  fun createTalk(@PathVariable userID: Long, @PathVariable osID: Long, @Valid @RequestBody createTalkRequestDTO: CreateTalkRequestDTO) =
      TalkResTranslator.translateFrom(openSpaceService.createTalk(userID, osID, createTalkRequestDTO))

  @DeleteMapping("/{openSpaceID}/talk/{talkID}/user/{userID}")
  fun deleteTalk(@PathVariable userID: Long, @PathVariable openSpaceID: Long, @PathVariable talkID: Long) =
      TalkResTranslator.translateFrom(openSpaceService.deleteTalk(talkID, openSpaceID, userID))

  @GetMapping("/user/{userID}")
  fun findAllByUser(@PathVariable userID: Long) =
      OpenSpaceResTranslator.translateAllFrom(openSpaceService.findAllByUser(userID))

  @GetMapping("/{id}")
  fun findById(@PathVariable id: Long) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.findById(id))

  @GetMapping("/talks/{userID}/{osID}")
  fun findTalksByUser(@PathVariable userID: Long, @PathVariable osID: Long) =
      TalkResTranslator.translateAllFrom(openSpaceService.findTalksOfUserInOpenSpace(userID, osID))

  @GetMapping("/talks/{id}")
  fun findTalks(@PathVariable id: Long) =
      TalkResTranslator.translateAllFrom(openSpaceService.findTalks(id))

  @GetMapping("/assignedSlots/{id}")
  fun findAssignedSlotsById(@PathVariable id: Long) = openSpaceService.findAssignedSlotsById(id)

  @PutMapping("/activateQueue/{userID}/{osID}")
  fun activateQueue(@PathVariable userID: Long, @PathVariable osID: Long) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.activateQueue(userID, osID))

  @PutMapping("/finishQueue/{userID}/{osID}")
  fun finishQueue(@PathVariable userID: Long, @PathVariable osID: Long) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.finishQueue(userID, osID))

  @PutMapping("/enqueueTalk/{userID}/{talkID}")
  fun enqueueTalk(@PathVariable userID: Long, @PathVariable talkID: Long) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.enqueueTalk(userID, talkID))

  @PutMapping("/{openSpaceId}/user/{userID}/callForPapers")
  fun callForPapers(@PathVariable userID: Long, @PathVariable openSpaceId: Long) =
      OpenSpaceResTranslator.translateFrom(openSpaceService.toggleCallForPapers(openSpaceId, userID))
}
