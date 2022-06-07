package com.sos.smartopenspace.controllers

import com.sos.smartopenspace.services.TalkService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalTime

@RestController
@RequestMapping("talk")
class TalkController(private val talkService: TalkService) {
  @PutMapping("/schedule/{userID}/{talkID}/{roomID}/{time}")
  fun scheduleTalk(
    @PathVariable userID: Long,
    @PathVariable talkID: Long,
    @PathVariable roomID: Long,
    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) time: LocalTime
  ) =
    talkService.scheduleTalk(talkID, roomID, time, userID)

  @PutMapping("/exchange/{talkID}/{roomID}/{time}")
  fun exchangeTalk(
    @PathVariable talkID: Long,
    @PathVariable roomID: Long,
    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) time: LocalTime
  ) =
    talkService.exchangeTalk(talkID, roomID, time)

  @PutMapping("/nextTalk/{userID}/{osID}")
  fun nextTalk(@PathVariable userID: Long, @PathVariable osID: Long) =
    talkService.nextTalk(userID, osID)

  @PutMapping("/{talkID}/user/{userID}/vote")
  fun voteTalk( @PathVariable talkID: Long, @PathVariable userID: Long) =
    talkService.voteTalk(talkID, userID)

  @PutMapping("/{talkID}/user/{userID}/unvote")
  fun unvoteTalk( @PathVariable talkID: Long, @PathVariable userID: Long) =
    talkService.unvoteTalk(talkID, userID)

}
