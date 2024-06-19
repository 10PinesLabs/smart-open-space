package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.dto.request.CreateReviewRequestDTO
import com.sos.smartopenspace.dto.request.CreateTalkRequestDTO
import com.sos.smartopenspace.services.TalkService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("talk")
class TalkController(private val talkService: TalkService) {

  @PutMapping("/schedule/{userID}/{talkID}/{slotID}/{roomID}")
  fun scheduleTalk(
    @PathVariable userID: Long,
    @PathVariable talkID: Long,
    @PathVariable slotID: Long,
    @PathVariable roomID: Long
  ) =
    talkService.scheduleTalk(talkID, userID, slotID, roomID)

  @PutMapping("/exchange/{talkID}/{slotID}/{roomID}")
  fun exchangeTalk(
    @PathVariable talkID: Long,
    @PathVariable slotID: Long,
    @PathVariable roomID: Long
  ) =
    talkService.exchangeTalk(talkID, roomID, slotID)

  @PutMapping("/nextTalk/{userID}/{osID}")
  fun nextTalk(@PathVariable userID: Long, @PathVariable osID: Long) =
    talkService.nextTalk(userID, osID)

  @GetMapping("/{talkID}")
  fun getTalk(@PathVariable talkID: Long) = talkService.getTalk(talkID)

  @PutMapping("/{talkId}/user/{userId}")
  fun updateTalk(@PathVariable talkId: Long, @PathVariable userId: Long, @Valid @RequestBody createTalkRequestDTO: CreateTalkRequestDTO) =
    talkService.updateTalk(talkId, userId, createTalkRequestDTO)
    
  @PutMapping("/{talkID}/user/{userID}/vote")
  fun voteTalk( @PathVariable talkID: Long, @PathVariable userID: Long) =
    talkService.voteTalk(talkID, userID)

  @PutMapping("/{talkID}/user/{userID}/unvote")
  fun unvoteTalk( @PathVariable talkID: Long, @PathVariable userID: Long) =
    talkService.unvoteTalk(talkID, userID)

  @PostMapping("/{talkID}/user/{userID}/review")
  fun reviewTalk( @PathVariable talkID: Long, @PathVariable userID: Long, @Valid @RequestBody createReviewRequestDTO: CreateReviewRequestDTO) =
    talkService.addReview(talkID, userID, createReviewRequestDTO)
}
