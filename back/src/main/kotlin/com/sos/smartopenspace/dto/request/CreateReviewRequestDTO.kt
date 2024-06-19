package com.sos.smartopenspace.dto.request
import javax.validation.constraints.*

class CreateReviewRequestDTO(
  @field:NotNull
  @field:Max(5)
  @field:Min(1)
  val grade: Int,

  val comment: String
)