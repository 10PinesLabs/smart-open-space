package com.sos.smartopenspace.persistence
import com.sos.smartopenspace.domain.Talk
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TalkRepository : CrudRepository<Talk, Long> {
  @Query("SELECT tk FROM OpenSpace os LEFT JOIN os.talks tk WHERE os.id = :openSpaceId ORDER BY tk.votingUsers.size desc")
  fun findAllByOpenSpaceIdOrderedByVotes(@Param("openSpaceId") openSpaceId: Long): List<Talk>
}