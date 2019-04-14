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
    val account = Account("2", "client1", Balance(50))
    val accountEntity = Marshal(account).to[MessageEntity].futureValue
    val requestForAccountCreation = Post("/accounts").withEntity(accountEntity)
    requestForAccountCreation ~> accRoutes
    // Post operation
    val operation = Operation("O1", "2", Withdrawal, amount = Amount(20))
    val opEntity = Marshal(operation).to[MessageEntity].futureValue
    val postOperationRequest = Post("/operations").withEntity(opEntity)
    postOperationRequest ~> opRoutes

    //get operations
    val request = HttpRequest(uri = "/operations/2")

    request ~> opRoutes ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[History].operations should have size 1
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
    val postOperationRequest = Post("/operations").withEntity(opEntity)
    postOperationRequest ~> opRoutes ~> check {
      status should ===(StatusCodes.Created)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"message":"Operation performed successfully!"}""")
    }

    //get Account
    val request = HttpRequest(uri = "/accounts/1")

    request ~> accRoutes ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"balance":{"balance":30},"clientName":"client1","no":"1"}""")
    }
  }

  "perform deposit for an existing account (POST /operations/3)" in {
    //First create account
    val account = Account("3", "client3", Balance(10))
    val accountEntity = Marshal(account).to[MessageEntity].futureValue
    val requestForAccountCreation = Post("/accounts").withEntity(accountEntity)
    requestForAccountCreation ~> accRoutes

    //post operations
    val operation = Operation("O1", "3", Deposit, amount = Amount(30))
    val opEntity = Marshal(operation).to[MessageEntity].futureValue
    val postOperationRequest = Post("/operations").withEntity(opEntity)
    postOperationRequest ~> opRoutes ~> check {
      status should ===(StatusCodes.Created)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"message":"Operation performed successfully!"}""")
    }

    //get Account
    val request = HttpRequest(uri = "/accounts/3")

    request ~> accRoutes ~> check {
      status should ===(StatusCodes.OK)

      contentType should ===(ContentTypes.`application/json`)

      entityAs[String] should ===("""{"balance":{"balance":40},"clientName":"client3","no":"3"}""")
    }
  }

}
