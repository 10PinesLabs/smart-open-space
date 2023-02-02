package com.sos.smartopenspace.domain

import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

@Entity
@Table(name = "assigned_slot")
class Assignment(
    @ManyToOne
  var slot: TalkSlot,
    @ManyToOne
  var room: Room,
    @OneToOne
  val talk: Talk,
    @Id
  @GeneratedValue
  val id: Long = 0
) {
  fun startAt(time: LocalTime) = slot.startTime == time

  fun moveTo(slot: TalkSlot, room: Room) {
    this.slot = slot
    this.room = room
  }

  fun hasDate(date: LocalDate?): Boolean {
    return slot.date == date
  }
}