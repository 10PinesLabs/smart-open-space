package com.sos.smartopenspace.services

import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.dto.request.CreateReviewRequestDTO
import com.sos.smartopenspace.dto.request.CreateTalkRequestDTO
import com.sos.smartopenspace.persistence.*
import com.sos.smartopenspace.websockets.QueueSocket
import com.sos.smartopenspace.websockets.ScheduleSocket
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TalkService(
        private val openSpaceRepository: OpenSpaceRepository,
        private val talkRepository: TalkRepository,
        private val roomRepository: RoomRepository,
        private val trackRepository: TrackRepository,
        private val userService: UserService,
        private val scheduleSocket: ScheduleSocket,
        private val queueSocket: QueueSocket,
        private val slotRepository: SlotRepository,
        private val updatableItemCollectionService: UpdatableItemCollectionService
) {
  private fun findUser(userID: Long) = userService.findById(userID)
  private fun findOpenSpace(id: Long) = openSpaceRepository.findByIdOrNull(id) ?: throw OpenSpaceNotFoundException()
  private fun findTalk(id: Long) = talkRepository.findByIdOrNull(id) ?: throw TalkNotFoundException()
  private fun findRoom(id: Long) = roomRepository.findByIdOrNull(id) ?: throw RoomNotFoundException()

  private fun findSlot(slotID: Long) =
    slotRepository.findByIdOrNull(slotID) ?: throw NotFoundException("No se encontro el slot con el id ${slotID}")

  fun scheduleTalk(talkID: Long, userID: Long, slotID: Long, roomID: Long): OpenSpace {
    val openSpace = openSpaceRepository.findFirstOpenSpaceByTalkId(talkID)
    openSpace.scheduleTalk(findTalk(talkID), findUser(userID), findSlot(slotID), findRoom(roomID))
    scheduleSocket.sendFor(openSpace)

    return openSpace
  }


  fun exchangeTalk(talkID: Long, roomID: Long, slotID: Long): OpenSpace {
    val openSpace = openSpaceRepository.findFirstOpenSpaceByTalkId(talkID)
    openSpace.exchangeSlot(findTalk(talkID), findRoom(roomID), findSlot(slotID))
    scheduleSocket.sendFor(openSpace)

    return openSpace
  }

  fun nextTalk(userID: Long, osID: Long): OpenSpace {
    val openSpace = findOpenSpace(osID).nextTalk(findUser(userID))
    queueSocket.sendFor(openSpace)
    return openSpace
  }

  fun voteTalk(talkID: Long, userID: Long): Talk {
    val aTalk = findTalk(talkID)
    if (aTalk.speaker.id == userID) throw UserCannotVoteItsTalkException()
    val aUser = findUser(userID)
    aTalk.addVoteBy(aUser)
    return aTalk
  }

  fun updateTalk(talkId: Long, userId: Long, createTalkRequestDTO: CreateTalkRequestDTO): Talk {
    val talk = findTalk(talkId)
    val track: Track? = findTrack(createTalkRequestDTO.trackId)
    val user = findUser(userId)

    user.checkOwnershipOf(talk)
    talk.update(
      name = createTalkRequestDTO.name,
      description = createTalkRequestDTO.description,
      meetingLink = createTalkRequestDTO.meetingLink,
      track = track,
      openSpace = openSpaceRepository.findFirstOpenSpaceByTalkId(talkId)
    )

    talk.updateDocuments(
      updatableItemCollectionService.getNewItems(createTalkRequestDTO.documents),
      updatableItemCollectionService.getDeletedItems(createTalkRequestDTO.documents, talk.documents)
      )

    return talk
  }

  fun unvoteTalk(talkID: Long, userID: Long): Talk {
    val talk = findTalk(talkID)
    val user = findUser(userID)

    talk.removeVoteBy(user)

    return talk
  }

  private fun findTrack(trackId: Long?): Track? {
    val track: Track? = trackId?.let {
      findTrackById(it)
    }
    return track
  }

  @Transactional(readOnly = true)
  fun findTrackById(id: Long) = trackRepository.findByIdOrNull(id) ?: throw TrackNotFoundException()


  fun getTalk(talkID: Long): Talk {
    return findTalk(talkID)
  }

  fun addReview(talkID: Long, userId: Long, createReviewRequestDTO: CreateReviewRequestDTO): Talk {
    val talk = findTalk(talkID)
    if (talk.reviews.any { it.reviewer.id == userId }) throw RepeatedReviewForTalkException()
    val reviewer = userService.findById(userId)

    val newReview = Review(
      grade = createReviewRequestDTO.grade,
      reviewer = reviewer,
      comment = createReviewRequestDTO.comment
      )

    talk.addReview(newReview)

    return talk
  }
}
