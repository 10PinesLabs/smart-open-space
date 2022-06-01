package com.sos.smartopenspace.persistence

import com.sos.smartopenspace.domain.Track
import org.springframework.data.repository.CrudRepository

interface TrackRepository : CrudRepository<Track, Long>
