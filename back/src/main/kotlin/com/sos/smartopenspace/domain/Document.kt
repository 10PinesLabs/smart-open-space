package com.sos.smartopenspace.domain

import java.net.URL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
class Document(
  @field:NotEmpty()
  var name: String,

  @Column(columnDefinition="VarChar")
  @field:NotNull
  var link: URL,

  @Id
  @GeneratedValue
  override val id: Long = 0
) : UpdatableItemCollection