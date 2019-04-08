package com.kata.sg.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kata.sg.actor.OperationActor
import com.kata.sg.model.{Operation, Withdrawal}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class OperationRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
    with OperationRoutes {

  override val operationActor: ActorRef =
    system.actorOf(OperationActor.props, "account")

  lazy val routes = operationRoutes

 "OperationRoutes" should {
   "return no operations for an inexistant account (GET /operations/1)" in {
     val request = HttpRequest(uri = "/operations/1")

     request ~> routes ~> check {
       status should ===(StatusCodes.OK)

       contentType should ===(ContentTypes.`application/json`)

       entityAs[String] should ===("""{"operations":[]}""")
     }
   }

   "be able to perform operation (POST /operations)" in {
     val operation = Operation("1", "2", Withdrawal)
     val operationEntity = Marshal(operation).to[MessageEntity].futureValue

     val request = Post("/operations").withEntity(operationEntity)

     request ~> routes ~> check {
       status should ===(StatusCodes.Created)

       // we expect the response to be json:
//       contentType should ===(ContentTypes.`application/json`)

       // and we know what message we're expecting back:
       entityAs[String] should ===("""{"message":"Account created successfully! Account number : 1"}""")
     }
   }
 }
}
