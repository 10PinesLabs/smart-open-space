package com.sos.smartopenspace.services

import com.sos.smartopenspace.domain.*
import com.sos.smartopenspace.dto.request.CreateTalkRequestDTO
import com.sos.smartopenspace.dto.request.OpenSpaceRequestDTO
import com.sos.smartopenspace.persistence.*
import com.sos.smartopenspace.translators.response.AssignedSlotResTranslator
import com.sos.smartopenspace.websockets.QueueSocket
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OpenSpaceService(
    private val openSpaceRepository: OpenSpaceRepository,
    private val talkRepository: TalkRepository,
    private val trackRepository: TrackRepository,
    private val userService: UserService,
    private val queueSocket: QueueSocket,
    private val updatableItemCollectionService: UpdatableItemCollectionService
) {
    private fun findUser(userID: Long) = userService.findById(userID)

    fun create(userID: Long, openSpaceRequestDTO: OpenSpaceRequestDTO): OpenSpace {
        val openSpace = OpenSpace(
            name = openSpaceRequestDTO.name,
            rooms = openSpaceRequestDTO.rooms.toMutableSet(),
            slots = openSpaceRequestDTO.slots.toMutableSet(),
            description = openSpaceRequestDTO.description,
            tracks = openSpaceRequestDTO.tracks.toMutableSet()
        )
        findUser(userID).addOpenSpace(openSpace)
        return openSpaceRepository.save(openSpace)
    }

    fun update(userID: Long, openSpaceID: Long, openSpaceRequestDTO: OpenSpaceRequestDTO): OpenSpace {
        val openSpace = findById(openSpaceID)
        val user = findUser(userID)

        openSpace.updateRooms(
            updatableItemCollectionService.getNewItems(openSpaceRequestDTO.rooms),
            updatableItemCollectionService.getDeletedItems(openSpaceRequestDTO.rooms, openSpace.rooms)
        )
        openSpace.updateSlots(
            updatableItemCollectionService.getNewItems(openSpaceRequestDTO.slots),
            updatableItemCollectionService.getDeletedItems(openSpaceRequestDTO.slots, openSpace.slots)
        )
        openSpace.updateTracks(
            updatableItemCollectionService.getNewItems(openSpaceRequestDTO.tracks),
            updatableItemCollectionService.getDeletedItems(openSpaceRequestDTO.tracks, openSpace.tracks)
        )
        openSpace.removeInvalidAssignedSlots()

        openSpace.update(
            user,
            name = openSpaceRequestDTO.name,
            description = openSpaceRequestDTO.description
        )

        return openSpace
    }

    fun delete(userID: Long, openSpaceID: Long): Long {
        val user = findUser(userID)
        val openSpace = findById(openSpaceID)

        user.checkOwnershipOf(openSpace)

        openSpaceRepository.delete(openSpace)

        return openSpace.id
    }

    @Transactional(readOnly = true)
    fun findAllByUser(userID: Long) = openSpaceRepository.findAllByOrganizerId(userID)

    @Transactional(readOnly = true)
    fun findById(id: Long): OpenSpace = openSpaceRepository.findById(id).orElseThrow { OpenSpaceNotFoundException() }

    @Transactional(readOnly = true)
    fun findTrackById(id: Long) = trackRepository.findByIdOrNull(id) ?: throw TrackNotFoundException()

    private fun findByTalk(talkID: Long) = openSpaceRepository.findFirstOpenSpaceByTalkId(talkID)
    private fun findTalk(id: Long) = talkRepository.findByIdOrNull(id) ?: throw TalkNotFoundException()

    @Transactional(readOnly = true)
    fun findTalks(id: Long) = talkRepository.findAllByOpenSpaceIdOrderedByVotes(id).mapNotNull { it }

    fun createTalk(userID: Long, osID: Long, createTalkRequestDTO: CreateTalkRequestDTO): Talk {
        val user = findUser(userID)
        val talk = createTalkFrom(createTalkRequestDTO, user = user)
        findById(osID).addTalk(talk)
        return talk
    }

    @Transactional(readOnly = true)
    fun findTalksOfUserInOpenSpace(userID: Long, openSpaceId: Long): List<Talk> {
        val openSpace = findById(openSpaceId)
        val user = findUser(userID)
        return openSpace.getUserTalks(user)
    }

    @Transactional(readOnly = true)
    fun findAssignedSlotsById(id: Long) = AssignedSlotResTranslator.translateAllFrom(findById(id).assignedSlots.toList())

    fun activateQueue(userID: Long, osID: Long) =
        findById(osID).activeQueue(findUser(userID))

    fun finishQueue(userID: Long, osID: Long) =
        findById(osID).finishQueuing(findUser(userID))

    fun enqueueTalk(userID: Long, talkID: Long): OpenSpace {
        val talk = findTalk(talkID)
        val openSpace = findByTalk(talkID)
        checkPermissions(talk, userID, openSpace)
        openSpace.enqueueTalk(talk)
        queueSocket.sendFor(openSpace)
        return openSpace
    }

    private fun checkPermissions(
        talk: Talk,
        userID: Long,
        openSpace: OpenSpace
    ) {
        (!userIsSpeakerOf(talk, userID) && userIsOrganizerOf(openSpace, userID)) && throw TalkNotFoundException()
    }

    private fun userIsOrganizerOf(openSpace: OpenSpace, userID: Long) =
        openSpace.organizer.id != userID

    private fun userIsSpeakerOf(talk: Talk, userID: Long) = talk.speaker.id == userID


    fun toggleCallForPapers(openSpaceId: Long, userID: Long): OpenSpace {
        val openSpace = findById(openSpaceId)
        val user = findUser(userID)
        openSpace.toggleCallForPapers(user)
        return openSpace
    }

    fun toggleVoting(openSpaceId: Long, userID: Long): OpenSpace {
        val openSpace = findById(openSpaceId)
        val user = findUser(userID)
        openSpace.toggleVoting(user)
        return openSpace
    }

    fun toggleShowSpeakerName(openSpaceId: Long, userID: Long): OpenSpace {
        val openSpace = findById(openSpaceId)
        val user = findUser(userID)
        openSpace.toggleShowSpeakerName(user)
        return openSpace
    }

    private fun createTalkFrom(createTalkRequestDTO: CreateTalkRequestDTO, user: User): Talk {
        val track: Track? = findTrack(createTalkRequestDTO.trackId)
        return Talk(
            name = createTalkRequestDTO.name,
            description = createTalkRequestDTO.description,
            meetingLink = createTalkRequestDTO.meetingLink,
            track = track,
            speaker = user,
            documents = createTalkRequestDTO.documents.toMutableSet()
        )
    }

    private fun findTrack(trackId: Long?): Track? {
        val track: Track? = trackId?.let {
            findTrackById(it)
        }
        return track
    }

    @Transactional
    fun deleteTalk(talkID: Long, openSpaceID: Long, userID: Long): Talk {
        val openSpace = findById(openSpaceID)
        val user = findUser(userID)
        val talk = findTalk(talkID)
        user.checkOwnershipOf(talk)

        openSpace.removeTalk(talk)
        talkRepository.delete(talk)
        return talk
    }

}