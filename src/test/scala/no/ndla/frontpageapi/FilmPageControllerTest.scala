package no.ndla.frontpageapi
import java.io.IOException
import java.net.ServerSocket

import cats.effect.IO
import no.ndla.frontpageapi.controller.NdlaMiddleware
import no.ndla.frontpageapi.model.api.FilmFrontPageData
import org.http4s.rho.swagger.syntax.{io => ioSwagger}
import org.http4s.server.blaze.BlazeBuilder
import scalaj.http.Http
import org.mockito.Mockito._

class FilmPageControllerTest extends UnitSuite with TestEnvironment {

  def findFreePort: Int = {
    def closeQuietly(socket: ServerSocket): Unit = {
      try {
        socket.close()
      } catch { case _: Throwable => }
    }
    var socket: ServerSocket = null
    try {
      socket = new ServerSocket(0)
      socket.setReuseAddress(true)
      val port = socket.getLocalPort
      closeQuietly(socket)
      return port;
    } catch {
      case e: IOException =>
        println("Failed to open socket")
    } finally {
      if (socket != null) {
        closeQuietly(socket)
      }
    }
    throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
  }

  val serverPort: Int = findFreePort

  override val filmPageController = new FilmPageController[IO](ioSwagger)
  BlazeBuilder[IO]
    .mountService(NdlaMiddleware(filmPageController.toService()), "/filmfrontpage")
    .bindLocal(serverPort)
    .start
    .unsafeRunSync()

  test("Should return 200 when frontpage exist") {
    when(readService.filmFrontPage(None)).thenReturn(Some(TestData.apiFilmFrontPage))
    val response = Http(s"http://localhost:$serverPort/filmfrontpage").method("GET").asString
    response.code should equal(200)
  }

  test("Should return 404 when no frontpage found") {
    when(readService.filmFrontPage(None)).thenReturn(None)
    val response = Http(s"http://localhost:$serverPort/filmfrontpage").method("GET").asString
    response.code should equal(404)
  }

}
