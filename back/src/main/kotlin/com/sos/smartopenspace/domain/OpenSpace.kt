package com.sos.smartopenspace.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

class AlreadyActivedQueuingException : RuntimeException("Encolamiento ya se encuentra activo")
class AnotherTalkIsEnqueuedException : RuntimeException("Existe otra charla encolada")
class BusySlotException : RuntimeException("Slot ocupado")
class CantFinishTalkException : RuntimeException("No podes terminar la charla actual")
class EmptyQueueException : RuntimeException("La cola de charlas está vacía")
class FinishedQueuingException : RuntimeException("Encolamiento finalizado")
class InactiveQueueException : RuntimeException("No está activo el encolamiento")
class NotTheOrganizerException : RuntimeException("No sos el organizador")
class SlotNotFoundException : RuntimeException("No existe un slot en ese horario")
class TalkAlreadyAssignedException : RuntimeException("Charla ya está agendada")
class TalkAlreadyEnqueuedException : RuntimeException("Charla ya está encolada")
class TalkDoesntBelongException : RuntimeException("Charla no pertence al Open Space")
class TalkIsNotForScheduledException : RuntimeException("Charla no está para agendar")
class TalkIsNotScheduledException : RuntimeException("Charla no está agendada")
class CallForPapersClosedException : RuntimeException("La convocatoria se encuentra cerrada")
class NotValidTrackForOpenSpaceException : RuntimeException("El track de la charla no pertenece a este open space")

@Entity
class OpenSpace(
  @field:NotEmpty(message = "Ingrese un nombre")
  @field:NotBlank(message = "Nombre no puede ser vacío")
  val name: String,
  val date: LocalDate,

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
  @OneToMany(mappedBy = "openSpace", cascade = [CascadeType.ALL])
  val talks: MutableSet<Talk> = mutableSetOf(),

  @field:Column(length=1000)
  @field:Size(min=0, max=1000)
  val description: String = "",

  @field:Valid
  @OneToMany(cascade = [CascadeType.ALL])
  @JoinColumn(name = "open_space_id")
  val tracks: Set<Track> = emptySet(),

  val urlImage: String = "",
  @Id @GeneratedValue
  val id: Long = 0
) {

  init {
    talks.forEach { it.openSpace = this }
  }

  @ManyToOne
  lateinit var organizer: User

  @JsonIgnore
  @OneToMany(cascade = [CascadeType.ALL])
  @JoinColumn(name = "open_space_id")
  val assignedSlots: MutableSet<AssignedSlot> = mutableSetOf()

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
    room to slots.filter { it.isAssignable() }.map { it.startTime }
  }.filter { it.second.isNotEmpty() }

  fun addTalk(talk: Talk): OpenSpace {
    checkIsFinishedQueue()
    checkIsActiveCallForPapers()
    if (tracksDefined(talk) && !tracks.contains(talk.track)) throw NotValidTrackForOpenSpaceException()
    talk.openSpace = this
    talks.add(talk)
    return this
  }

  private fun tracksDefined(talk: Talk) =
    !(tracks.isEmpty() && talk.track == null)

  fun containsTalk(talk: Talk) = talks.contains(talk)

  private fun checkIsActiveCallForPapers() {
    if (!isActiveCallForPapers )
      throw CallForPapersClosedException()
  }

  private fun isBusySlot(room: Room, time: LocalTime) = assignedSlots.any { it.startAt(time) && it.room == room }

  private fun checkTalkBelongs(talk: Talk) {
    if (!containsTalk(talk))
      throw TalkDoesntBelongException()
  }

  private fun checkScheduleTalk(talk: Talk, time: LocalTime, room: Room, user: User) {
    checkTalkBelongs(talk)
    assignedSlots.any { it.talk == talk } && throw TalkAlreadyAssignedException()
    !toSchedule.contains(talk) && !isOrganizer(user) && throw TalkIsNotForScheduledException()
    isBusySlot(room, time) && throw BusySlotException()
  }

  private fun findTalkSlot(time: LocalTime) = slots.find { it.startTime == time && it.isAssignable() } as TalkSlot? ?: throw SlotNotFoundException()

  fun scheduleTalk(talk: Talk, time: LocalTime, room: Room, user: User): AssignedSlot {
    val slot = findTalkSlot(time)
    checkScheduleTalk(talk, time, room, user)
    val assignedSlot = AssignedSlot(slot, room, talk)
    assignedSlots.add(assignedSlot)
    toSchedule.remove(talk)
    return assignedSlot
  }

  fun exchangeSlot(talk: Talk, time: LocalTime, room: Room) {
    val slot = findTalkSlot(time)
    val current = assignedSlots.find { it.talk == talk } ?: throw TalkIsNotScheduledException()
    assignedSlots.find { it.room == room && it.slot == slot }?.moveTo(current.slot, current.room)
    current.moveTo(slot, room)
  }

  @JsonProperty
  fun freeSlots() = rooms.map { room ->
    room to slots.filter {
      it.isAssignable() && !isBusySlot(room, it.startTime)
    }.map { it.startTime }
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
}

enum class QueueState {
  PENDING,
  ACTIVE,
  FINISHED
}
