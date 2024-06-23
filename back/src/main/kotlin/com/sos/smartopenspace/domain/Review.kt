package com.sos.smartopenspace.domain

import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Entity
class Review (

  @field:Valid
  @field:NotNull
  @field:Max(5)
  @field:Min(1)
  val grade: Int,

  @ManyToOne
  val reviewer: User,

  @Column(columnDefinition="VarChar")
  val comment: String?,

  @Id
  @GeneratedValue
  val id: Long = 0
)