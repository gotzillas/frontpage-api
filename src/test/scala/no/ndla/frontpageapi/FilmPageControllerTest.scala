package no.ndla.frontpageapi
import cats.effect.IO
import no.ndla.frontpageapi.controller.NdlaMiddleware
import no.ndla.frontpageapi.model.api.FilmFrontPageData
import org.http4s.rho.swagger.syntax.{io => ioSwagger}
import org.http4s.server.blaze.BlazeBuilder
import scalaj.http.Http
import org.mockito.Mockito._

class FilmPageControllerTest extends UnitSuite with TestEnvironment {
  override val filmPageController = new FilmPageController[IO](ioSwagger)
  BlazeBuilder[IO]
    .mountService(NdlaMiddleware(filmPageController.toService()), "/filmfrontpage")
    .bindLocal(4545)
    .start
    .unsafeRunSync()

  test("Should return 200 when frontpage exist") {
    when(readService.filmFrontPage(None)).thenReturn(Some(TestData.apiFilmFrontPage))
    val response = Http("http://localhost:4545/filmfrontpage").method("GET").asString
    response.code should equal(200)
  }

  test("Should return 404 when no frontpage found") {
    when(readService.filmFrontPage(None)).thenReturn(None)
    val response = Http("http://localhost:4545/filmfrontpage").method("GET").asString
    response.code should equal(404)
  }

}
