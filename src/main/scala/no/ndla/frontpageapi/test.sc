import io.circe.generic.auto._
import io.circe.syntax._

case class Person(name: String)
case class Greeting(salutation: String, person: Person, exclamationMarks: Int)


Greeting("Hey", Person("Chris"), 3).asJson

