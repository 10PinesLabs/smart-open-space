package com.sos.smartopenspace.persistence

import com.sos.smartopenspace.domain.Talk
import org.springframework.data.repository.CrudRepository

interface TalkRepository : CrudRepository<Talk, Long>