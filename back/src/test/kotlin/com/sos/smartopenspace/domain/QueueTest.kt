package com.sos.smartopenspace.domain

import com.sos.smartopenspace.anOpenSpace
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class QueueTest {

  private fun anyUser(): User {
    return User("augusto@sos.sos", "augusto", "Augusto")
  }
  private fun anyTalk(user: User) = Talk("Talk1", speaker = user)

  private fun anyOSWithActiveQueue(talks: MutableSet<Talk> = mutableSetOf()): OpenSpace {
    val user = anyUser()
    val os = anOpenSpace(talks = talks, organizer = user)
    os.activeQueue(user)
    return os
  }

  @Test
  fun `No puedo activar encolamiento si no soy el organizador`() {
    val user = anyUser()
    val openSpace = anOpenSpace()
    assertThrows(NotTheOrganizerException::class.java) {
      openSpace.activeQueue(user)
    }
  }

  @Test
  fun `Se crea un nuevo openSpace tiene el encolamiento pendiente`() {
    assertTrue(anOpenSpace().isPendingQueue())
  }

  @Test
  fun `Como organizador activo encolamiento`() {
    assertTrue(anyOSWithActiveQueue().isActiveQueue())
  }

  @Test
  fun `Os sin charlas encoladas, no tiene charla actual`() {
    val os = anOpenSpace()
    assertNull(os.currentTalk())
  }

  @Test
  fun `Os sin charlas encoladas, no puedo pasar a la siguiente charla`() {
    val os = anOpenSpace()
    val user = anyUser()
    assertThrows(EmptyQueueException::class.java) {
      os.nextTalk(user)
    }
  }

  @Test
  fun `Encolar una charla`() {
    val user = anyUser()
    val talk = anyTalk(user)
    val os = anyOSWithActiveQueue(mutableSetOf(talk))
    os.enqueueTalk(talk)
    assertEquals(talk, os.currentTalk())
  }

  @Test
  fun `No puedo encolar una charla que no pertenece al os`() {
    val user = anyUser()
    val talk = anyTalk(user)
    val os = anyOSWithActiveQueue()
    assertThrows(TalkDoesntBelongException::class.java) {
      os.enqueueTalk(talk)
    }
  }

  @Test
  fun `No puedo encolar la misma charla 2 veces`() {
    val user = anyUser()
    val talk = anyTalk(user)
    val os = anyOSWithActiveQueue(mutableSetOf(talk))
    os.enqueueTalk(talk)
    assertThrows(TalkAlreadyEnqueuedException::class.java) {
      os.enqueueTalk(talk)
    }
  }

  @Test
  fun `No puedo encolar una segunda charla del mismo speaker`() {
    val user = anyUser()
    val talk1 = anyTalk(user)
    val talk2 = anyTalk(user)
    val os = anyOSWithActiveQueue(mutableSetOf(talk1, talk2))
    anyUser()
    os.enqueueTalk(talk1)
    assertThrows(AnotherTalkIsEnqueuedException::class.java) {
      os.enqueueTalk(talk2)
    }
  }

  @Test
  fun `Charlas se encolan en orden`() {
    val speaker1 = anyUser()
    val speaker2 = anyUser()
    val speaker3 = anyUser()
    val talk1 = anyTalk(speaker1)
    val talk2 = anyTalk(speaker2)
    val talk3 = anyTalk(speaker3)
    val os = anyOSWithActiveQueue(mutableSetOf(talk1, talk2, talk3))
    os.enqueueTalk(talk1)
    os.enqueueTalk(talk2)
    os.enqueueTalk(talk3)
    assertIterableEquals(listOf(talk1, talk2, talk3), os.queue)
  }

  @Test
  fun `El speaker actual puede terminar la charla`() {
    val speaker1 = anyUser()
    val speaker2 = anyUser()
    val talk1 = anyTalk(speaker1)
    val talk2 = anyTalk(speaker2)
    val os = anyOSWithActiveQueue(mutableSetOf(talk1, talk2))

    os.enqueueTalk(talk1)
    os.enqueueTalk(talk2)
    os.nextTalk(speaker1)
    assertEquals(talk2, os.currentTalk())
  }

  @Test
  fun `El organizador puede terminar la charla`() {
    val speaker1 = anyUser()
    val speaker2 = anyUser()
    val talk1 = anyTalk(speaker1)
    val talk2 = anyTalk(speaker2)
    val organizer = anyUser()
    val os = anOpenSpace(talks = mutableSetOf(talk1, talk2), organizer = organizer)
    os.activeQueue(organizer)
    os.enqueueTalk(talk1)
    os.enqueueTalk(talk2)
    os.nextTalk(organizer)
    assertEquals(talk2, os.currentTalk())
  }

  @Test
  fun `Si no soy el organizador o el speaker, no puedo terminar la charla`() {
    val speaker1 = anyUser()
    val speaker2 = anyUser()
    val talk1 = anyTalk(speaker1)
    val talk2 = anyTalk(speaker2)
    val os = anyOSWithActiveQueue(mutableSetOf(talk1, talk2))
    os.enqueueTalk(talk1)
    os.enqueueTalk(talk2)
    assertThrows(CantFinishTalkException::class.java) {
      os.nextTalk(speaker2)
    }
  }

  @Test
  fun `Termina la primer charla es el turno de la segunda`() {
    val speaker1 = anyUser()
    val speaker2 = anyUser()
    val talk1 = anyTalk(speaker1)
    val talk2 = anyTalk(speaker2)
    val os = anyOSWithActiveQueue(mutableSetOf(talk1, talk2))
    anyUser()
    os.enqueueTalk(talk1)
    os.enqueueTalk(talk2)
    os.nextTalk(speaker1)
    assertEquals(talk2, os.currentTalk())
  }

  @Test
  fun `Termina la primer charla, esta disponible para agendar`() {
    val speaker = anyUser()
    val talk = anyTalk(speaker)
    val os = anyOSWithActiveQueue(mutableSetOf(talk))
    os.enqueueTalk(talk)
    os.nextTalk(speaker)
    assertIterableEquals(setOf(talk), os.toSchedule)
  }

  @Test
  fun `No se puede encolar una charla, si no esta activo el encolamiento`() {
    val user = anyUser()
    val talk = anyTalk(user)
    val os = anOpenSpace(talks = mutableSetOf(talk))
    assertThrows(InactiveQueueException::class.java) {
      os.enqueueTalk(talk)
    }
  }

  @Test
  fun `Solo el organizador puede cerrar el encolamiento`() {
    val os = anOpenSpace()
    anyUser()
    assertThrows(NotTheOrganizerException::class.java) {
      os.finishQueuing(anyUser())
    }
  }

  @Test
  fun `Cuando cierra el encolamiento, la queue esta finalizada`() {
    val user = anyUser()
    val talk = anyTalk(user)
    val organizer = anyUser()
    val os = anOpenSpace(talks = mutableSetOf(talk), organizer = organizer)
    os.finishQueuing(organizer)
    assertTrue(os.isFinishedQueue())
  }

  @Test
  fun `Cuando cierra el encolamiento, no se pueden encolar charlas`() {
    val user = anyUser()
    val talk = anyTalk(user)
    val organizer = anyUser()
    val os = anOpenSpace(talks = mutableSetOf(talk), organizer = organizer)
    os.finishQueuing(organizer)
    assertThrows(FinishedQueuingException::class.java) {
      os.enqueueTalk(talk)
    }
  }

  @Test
  fun `Cuando cierra el encolamiento, no se pueden cargar charlas`() {
    val organizer = anyUser()
    val openSpace = anOpenSpace(organizer = organizer)
    openSpace.finishQueuing(organizer)
    assertThrows(FinishedQueuingException::class.java) {
      openSpace.addTalk(anyTalk(organizer))
    }
  }

  @Test
  fun `No puedo activar el encolamiento dos veces`() {
    val organizer = anyUser()
    val openSpace = anOpenSpace(organizer = organizer)
    openSpace.activeQueue(organizer)
    assertThrows(AlreadyActivedQueuingException::class.java) {
      openSpace.activeQueue(organizer)
    }
  }

  @Test
  fun `Se cierra el encolamiento, se descartan las charlas en la cola`() {
    val organizer = anyUser()
    val speaker2 = anyUser()
    val talk1 = anyTalk(organizer)
    val talk2 = anyTalk(speaker2)
    val os = anOpenSpace(talks = mutableSetOf(talk1, talk2), organizer = organizer)
    os.activeQueue(organizer)
    os.enqueueTalk(talk1)
    os.enqueueTalk(talk2)
    os.finishQueuing(organizer)
    assertTrue(os.queue.isEmpty())
    assertNull(os.currentTalk())
  }
}
