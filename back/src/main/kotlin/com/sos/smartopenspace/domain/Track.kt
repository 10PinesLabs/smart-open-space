package com.sos.smartopenspace.domain

import com.sos.smartopenspace.validators.HexColor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

@Entity
data class Track(
    @field:NotEmpty
    val name: String,

    @field:Size(max=500)
    val description: String = "",

    @field:HexColor
    val color: String,
    
    @Id
    @GeneratedValue
    override var id: Long = 0
) : UpdatableItemCollection
