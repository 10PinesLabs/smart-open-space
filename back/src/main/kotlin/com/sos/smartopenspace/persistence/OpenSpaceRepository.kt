package com.sos.smartopenspace.persistence

import com.sos.smartopenspace.domain.OpenSpace
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface OpenSpaceRepository : CrudRepository<OpenSpace, Long> {

    @Query("SELECT os FROM OpenSpace os join os.talks tk WHERE tk.id = :talkId")
    fun findFirstOpenSpaceByTalkId(@Param("talkId") talkId: Long): OpenSpace
}
