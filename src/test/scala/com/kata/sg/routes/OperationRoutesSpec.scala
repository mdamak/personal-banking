package com.kata.sg.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kata.sg.actor.{AccountActor, OperationActor}
import com.kata.sg.model._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class OperationRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with OperationRoutes with AccountRoutes {

  override val operationActor: ActorRef =
    system.actorOf(OperationActor.props, "operation")

  override val accountActor: ActorRef =
    system.actorOf(AccountActor.props, "account")

  lazy val opRoutes = operationRoutes
  lazy val accRoutes = accountRoutes

  "OperationRoutes" should {
    "return no operations for an unexistant account (GET /operations/1)" in {
      val request = HttpRequest(uri = "/operations/1")

      request ~> opRoutes ~> check {
        status should ===(StatusCodes.OK)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"operations":[]}""")
      }
    }
  }

  "get operations for an existing account (GET /operations/1)" in {
    //First create account
    val account = Account("1", "client1")
    val accountEntity = Marshal(account).to[MessageEntity].futureValue
    val requestForAccountCreation = Post("/accounts").withEntity(accountEntity)
    requestForAccountCreation ~> accRoutes
    //get operations
    val request = HttpRequest(uri = "/operations/1")

    request ~> opRoutes ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"operations":[]}""")
    }
  }

  "perform withdrawal for an existing account (POST /operations/1)" in {
    //First create account
    val account = Account("1", "client1", Balance(50))
    val accountEntity = Marshal(account).to[MessageEntity].futureValue
    val requestForAccountCreation = Post("/accounts").withEntity(accountEntity)
    requestForAccountCreation ~> accRoutes

    //post operations
    val operation = Operation("O1", "1", Withdrawal, amount = Amount(20))
    val opEntity = Marshal(operation).to[MessageEntity].futureValue
    val request = Post("/operations").withEntity(opEntity)
    request ~> opRoutes ~> check {
      status should ===(StatusCodes.Created)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"message":"Operation performed successfully!"}""")
    }
  }

}
