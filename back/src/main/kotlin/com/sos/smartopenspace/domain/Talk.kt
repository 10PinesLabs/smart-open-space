package com.sos.smartopenspace.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.net.URL
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

@Entity
class Talk(
  @field:NotEmpty(message = "Ingrese un nombre")
  @field:NotBlank(message = "Nombre no puede ser vacío")
  var name: String,

  @Column(columnDefinition = "VarChar")
  var description: String = "",

  @Id
  @GeneratedValue
  val id: Long = 0,

  var meetingLink: URL? = null,

  @field:Valid
  @ManyToOne
  var track: Track? = null
) {

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "vote",
          joinColumns = [JoinColumn(name = "talk_id", referencedColumnName = "id")],
          inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
  var votingUsers: MutableSet<User> = mutableSetOf()

  @ManyToOne
  lateinit var speaker: User

  fun update(openSpace: OpenSpace, name: String, description: String, meetingLink: URL? = null, track: Track? = null) {
    openSpace.checkTrackIsValid(track)
    this.name = name
    this.description = description
    this.meetingLink = meetingLink
    this.track = track
  }
  
  @JsonProperty
  fun votes(): Int {
    return votingUsers.size
  }

  fun addVoteBy(user: User) {
    votingUsers.add(user)
  }

  fun removeVoteBy(user: User) {
    if (!votingUsers.contains(user))
      throw UserDidntVoteThisTalkException()

    votingUsers.remove(user)
  }
}
