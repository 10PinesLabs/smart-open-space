package com.sos.smartopenspace.websockets

import com.fasterxml.jackson.databind.ObjectMapper
import com.sos.smartopenspace.domain.OpenSpace
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

abstract class AbstractSocket<T>(private val objectMapper: ObjectMapper) : TextWebSocketHandler() {
  private val sessionList = mutableMapOf<WebSocketSession, Long>()

  override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
    sessionList -= session
  }

  abstract fun getData(id: Long): T

  abstract fun getData(os: OpenSpace): T

  override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
    val id = objectMapper.readTree(message.payload).asLong()
    sessionList[session] = id
    emit(session, getData(id))
  }

  fun sendFor(os: OpenSpace) {
    val data = getData(os)
    sessionList.filterValues { it == os.id }.keys.forEach { emit(it, data) }
  }

  private fun emit(session: WebSocketSession, data: T) =
    session.sendMessage(TextMessage(objectMapper.writeValueAsString(data)))
}