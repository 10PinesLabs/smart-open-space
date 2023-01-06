package com.sos.smartopenspace.config

import com.sos.smartopenspace.websockets.QueueSocket
import com.sos.smartopenspace.websockets.ScheduleSocket
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
  private val scheduleSocket: ScheduleSocket,
  private val queueSocket: QueueSocket
) : WebSocketConfigurer {
  override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
    registry
      .addHandler(scheduleSocket, "/scheduleSocket")
      .setAllowedOrigins("http://localhost:1234", "https://smartopenspace.herokuapp.com", "https://openspaces.10pines.com", "https://smartopenspace-10pines.herokuapp.com", "https://smartopenspace.10pines.com")
      .withSockJS()
    registry
      .addHandler(queueSocket, "/queueSocket")
      .setAllowedOrigins("http://localhost:1234", "https://smartopenspace.herokuapp.com", "https://openspaces.10pines.com", "https://smartopenspace-10pines.herokuapp.com", "https://smartopenspace.10pines.com")
      .withSockJS()
  }
}
