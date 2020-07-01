package server

import com.corundumstudio.socketio.listener.DataListener
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}


class LectureServer {

  val config: Configuration = new Configuration {
    setHostname("localhost")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)
  var total: Int = 0

  server.addEventListener("increment", classOf[String], new MessageListener())
  server.addEventListener("stop_server", classOf[Nothing], new StopListener(this))

  server.start()

  class MessageListener() extends DataListener[String] {
    override def onData(socket: SocketIOClient, data: String, ackRequest: AckRequest): Unit = {
      println("received message: " + data + " from " + socket)
      socket.sendEvent("ACK", "I received your message of " + data)
      total += 1
    }
  }

  class StopListener(server: LectureServer) extends DataListener[Nothing] {
    override def onData(socket: SocketIOClient, data: Nothing, ackRequest: AckRequest): Unit = {
      server.server.getBroadcastOperations.sendEvent("server_stopped")
      println("stopping server")
      server.server.stop()
    }
  }

  def numberOfMessages(): Int = {
    total
  }
}
