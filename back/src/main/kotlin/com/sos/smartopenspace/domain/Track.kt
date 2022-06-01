package com.sos.smartopenspace.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.sos.smartopenspace.Validators.HexColor
import javax.persistence.*
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@Entity
class Track(
    @field:NotEmpty
    val name: String,

    @field:Size(max=500)
    val description: String = "",

    @field:HexColor
    val color: String,
    @Id
    @GeneratedValue
    var id: Long = 0
) {
    @JsonIgnore
    @OneToMany(mappedBy = "track", cascade = [CascadeType.ALL])
    val talks: MutableSet<Talk> = mutableSetOf()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false

        val otherTalk: Track = other as Track

        return otherTalk.name == this.name &&
                otherTalk.color == this.color &&
                otherTalk.description == this.description
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}