package liu.se.scala_person_api

import cats._
import org.http4s.circe._     // decoders and encoders for json 
import io.circe.syntax._      // automatic case class generation
import org.http4s.dsl._
import org.http4s.implicits._ 
import org.http4s.blaze.server.BlazeServerBuilder
import _root_.io.circe.{Encoder}
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import _root_.io.circe.literal._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties

object Main extends IOApp{

  case class Person(var key: Int,var name: String);
  implicit val PersonEncoder: Encoder[Person] =
    Encoder.instance { person: Person =>
      json"""{"key": ${person.key}, "name": ${person.name}}"""
    }

  val TOPIC_NAME = "rest-requests"
  val KAFKA_SERVER = "localhost:9092"

   val  props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  def producerLog(log:String) : Unit = {
    val record = new ProducerRecord(TOPIC_NAME, "key", log)
    producer.send(record)
    producer.flush()
  }

  val personDB2 = collection.mutable.ListBuffer[Person]();

  // Request -> F[Response] (route)
  // these request may have side effects so we try to have
  // referential transparency through wrapping responses that we will
  // return into an effect type that we call F
  // so we return F of response
  // F is a generigh higher type
  // we intanceiate F when we start the app


  // some requests wont have responses so instead we F[Option[Response]]
  // bc we might not know what to resonse with
  // HttpRoutes[F] denotes F[Option[Response]]
  object PersonNameQueryMatcher extends QueryParamDecoderMatcher[String]("name")
  object PersonKeyQueryMatcher extends QueryParamDecoderMatcher[Int]("key")

  def personRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._ 

    // function to decompose queries
    // -> (thin arrow) means object with unapplied method
    // ojbect being mapped is the path 3230
    HttpRoutes.of[F] { 
      case GET -> Root / "persons" / "list" => 
        //Ok(List(List(personDB.values, personDB.key)).asJson)
        producerLog("SCALA_PERSON_API: List of persons requested.");
        Ok(personDB2.asJson) 
      case GET -> Root / "persons" / "find.name" :? PersonNameQueryMatcher(name) => 
        var found_person = new Person(-1, "null")
        for (person <- personDB2)
        {
          if(person.name == name)
          {
            found_person = person
          }
        }

        if(found_person.key != -1)
        {
          producerLog(s"SCALA_PERSON_API: Person: ${name} was requested.");
          Ok(found_person.asJson)
        } else 
        {
          producerLog(s"SCALA_PERSON_API: Unknown person: ${name} was requested.");
          Ok(found_person.name)
        }


      case GET -> Root / "persons" / "find.key" :? PersonKeyQueryMatcher(key) => 
        var found_person = new Person(-1, "null")
        for (person <- personDB2) 
        {
          if(person.key == key)
          {
            found_person = person;
          }
        }
        if(found_person.key != -1)
        {
          producerLog(s"SCALA_PERSON_API: Person key: ${key} was requested.");
          Ok(found_person.asJson)
        } else
        {
          producerLog(s"SCALA_PERSON_API: Unknown person key: ${key} was requested.");
          Ok(found_person.name)
        }
    } 
  } 

  def allRoutes[F[_] : Monad]: HttpRoutes[F] = 
    personRoutes // <+> use to chain other routes

  
  def allRoutesComplete[F[_] : Monad]:HttpApp[F] =
    allRoutes[F].orNotFound // return 404 if neither of allRoutes match

  
  def initDb() :Unit =
  {
    personDB2 += new Person(1, "Jakob Pogulis");
    personDB2 += new Person(2, "Xena");
    personDB2 += new Person(3, "Marcus Bendtsen");
    personDB2 += new Person(4, "Zorro");
    personDB2 += new Person(5, "Q");
  }


  override def run(args: List[String]): IO[ExitCode] = {
    initDb();
    BlazeServerBuilder[IO]
    .bindHttp(8060, "localhost")
    .withHttpApp(allRoutesComplete)
    .resource
    .use(_ => IO.never)
    .as(ExitCode.Success)
  }
}