package com.sos.smartopenspace.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@Entity
class OpenSpace(
  @field:NotEmpty(message = "Ingrese un nombre")
  @field:NotBlank(message = "Nombre no puede ser vacío")
  val name: String,

  @field:Valid
  @field:NotEmpty(message = "Ingrese al menos una sala")
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @OneToMany(cascade = [CascadeType.ALL])
  @JoinColumn(name = "open_space_id")
  val rooms: Set<Room>,

  @field:Valid
  @field:NotEmpty(message = "Ingrese al menos un slot")
  @OneToMany(cascade = [CascadeType.ALL])
  @JoinColumn(name = "open_space_id")
  val slots: Set<Slot>,

  @JsonIgnore
  @field:Valid
  @OneToMany(cascade = [CascadeType.ALL])
  @JoinColumn(name = "open_space_id")
  val talks: MutableSet<Talk> = mutableSetOf(),

  @field:Column(length = 1000)
  @field:Size(min = 0, max = 1000)
  val description: String = "",

  @field:Valid
  @OneToMany(cascade = [CascadeType.ALL])
  @JoinColumn(name = "open_space_id")
  val tracks: Set<Track> = emptySet(),

  val urlImage: String = "",

  @Id @GeneratedValue
  val id: Long = 0,

  @ManyToOne
  val organizer: User
) {

  @JsonIgnore
  @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
  @JoinColumn(name = "open_space_id")
  val assignments: MutableSet<Assignment> = mutableSetOf()

  @OrderColumn
  @JsonIgnore
  @OneToMany(fetch = FetchType.EAGER)
  val queue: MutableList<Talk> = mutableListOf()

  @OneToMany
  val toSchedule: MutableSet<Talk> = mutableSetOf()

  @Enumerated(EnumType.STRING)
  var queueState: QueueState = QueueState.PENDING

  var isActiveCallForPapers: Boolean = false

  fun isPendingQueue() = queueState == QueueState.PENDING
  fun isActiveQueue() = queueState == QueueState.ACTIVE
  fun isFinishedQueue() = queueState == QueueState.FINISHED

  @JsonProperty
  fun startTime() = slots.map { it.startTime }.min()

  @JsonProperty
  fun endTime() = slots.map { it.endTime }.max()

  @JsonProperty
  fun assignableSlots() = rooms.map { room ->
    room to slots.filter { it.isAssignable() }
  }.filter { it.second.isNotEmpty() }

  fun addTalk(talk: Talk): OpenSpace {
    checkIsFinishedQueue()
    checkIsActiveCallForPapers()
    checkTrackIsValid(talk.track)
    talks.add(talk)
    return this
  }

  fun checkTrackIsValid(track: Track?) {
    if (!isTrackValid(track))
      throw NotValidTrackForOpenSpaceException()
  }

  private fun isTrackValid(track: Track?) =
    !(areTracksUsed(track) && !trackIsFromThisOpenSpace(track))

  private fun trackIsFromThisOpenSpace(track: Track?) = tracks.any { it == track }

  private fun areTracksUsed(track: Track?) =
    !(tracks.isEmpty() && track == null)

  fun containsTalk(talk: Talk) = talks.contains(talk)
  fun containsSlot(slot: TalkSlot) = slots.contains(slot)

  private fun checkIsActiveCallForPapers() {
    if (!isActiveCallForPapers)
      throw CallForPapersClosedException()
  }

  private fun isBusySlot(room: Room, slot: Slot) = assignments.any { it.startAt(slot.startTime) && it.hasDate(slot.date) && it.room == room }

  private fun checkTalkBelongs(talk: Talk) {
    if (!containsTalk(talk))
      throw TalkDoesntBelongException()
  }
  private fun checkSlotBelongsToTheScheduleGrid(slot: TalkSlot) {
    if (!containsSlot(slot))
      throw SlotNotFoundException()
  }

  private fun checkScheduleTalk(talk: Talk, user: User, slot: TalkSlot, room: Room) {
    checkTalkBelongs(talk)
    checkSlotBelongsToTheScheduleGrid(slot)
    assignments.any { it.talk == talk } && throw TalkAlreadyAssignedException()
    !toSchedule.contains(talk) && !isOrganizer(user) && throw TalkIsNotForScheduledException()
    isBusySlot(room, slot) && throw BusySlotException()
  }

  fun scheduleTalk(talk: Talk, user: User, slot: TalkSlot, room: Room): Assignment {
    checkScheduleTalk(talk, user, slot, room)
    val assignment = Assignment(slot, room, talk)
    assignments.add(assignment)
    toSchedule.remove(talk)
    return assignment
  }

  fun exchangeSlot(talk: Talk, room: Room, slot: TalkSlot) {
    checkSlotBelongsToTheScheduleGrid(slot)
    val current = assignments.find { it.talk == talk } ?: throw TalkIsNotScheduledException()
    assignments.find { it.room == room && it.slot == slot }?.moveTo(current.slot, current.room)
    current.moveTo(slot, room)
  }

  @JsonProperty
  fun freeSlots() = rooms.map { room ->
    room to slots.filter {
      it.isAssignable() && !isBusySlot(room, it)
    }
  }.filter { it.second.isNotEmpty() }

  private fun checkIsOrganizer(user: User) = !isOrganizer(user) && throw NotTheOrganizerException()

  fun activeQueue(user: User): OpenSpace {
    !isPendingQueue() && throw AlreadyActivedQueuingException()
    checkIsOrganizer(user)
    queueState = QueueState.ACTIVE
    return this
  }

  fun currentTalk() = queue.firstOrNull()

  fun checkIsFinishedQueue() = isFinishedQueue() && throw FinishedQueuingException()

  fun enqueueTalk(talk: Talk): OpenSpace {
    isPendingQueue() && throw InactiveQueueException()
    checkIsFinishedQueue()
    checkTalkBelongs(talk)
    queue.contains(talk) && throw TalkAlreadyEnqueuedException()
    queue.any { it.speaker == talk.speaker } && throw AnotherTalkIsEnqueuedException()
    queue.add(talk)
    return this
  }

  private fun isCurrentSpeaker(user: User) = user == currentTalk()?.speaker
  private fun isOrganizer(user: User) = user == organizer

  fun nextTalk(user: User): OpenSpace {
    queue.isEmpty() && throw EmptyQueueException()
    !isCurrentSpeaker(user) && !isOrganizer(user) && throw CantFinishTalkException()
    toSchedule.add(queue.removeAt(0))
    return this
  }

  fun finishQueuing(user: User): OpenSpace {
    checkIsOrganizer(user)
    queueState = QueueState.FINISHED
    queue.clear()
    return this
  }

  fun toggleCallForPapers(user: User) {
    checkIsOrganizer(user)
    isActiveCallForPapers = !isActiveCallForPapers
  }

  @JsonProperty
  fun amountOfTalks(): Int {
    return talks.size
  }

  @JsonProperty
  fun startDate(): LocalDate? {
    return Collections.min(dates())
  }

  @JsonProperty
  fun endDate(): LocalDate? {
    return Collections.max(dates())
  }

  @JsonProperty
  fun dates(): Set<LocalDate?> {
    return slots.map { it.date }.toSet()
  }

  fun getUserTalks(user: User): List<Talk> {
    return talks.filter { talk -> user.isOwnerOf(talk) }
  }

  fun removeTalk(talk: Talk) {
    assignments.removeIf { it.talk.id == talk.id }
    queue.remove(talk)
    toSchedule.remove(talk)
    talks.remove(talk)
  }

  fun hasTalksToScheduled(): Boolean {
    return toSchedule.isNotEmpty()
  }

  fun hasQueuedTalks(): Boolean {
    return queue.isNotEmpty()
  }

  fun hasAssignedSlots(): Boolean {
    return assignments.isNotEmpty()
  }
}


enum class QueueState {
  PENDING,
  ACTIVE,
  FINISHED
}
