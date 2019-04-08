package com.kata.sg.routes

import akka.actor.ActorRef
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.kata.sg.actor.AccountActor
import com.kata.sg.model.Account
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}

class AccountRoutesSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest
  with AccountRoutes {

  override val accountActor: ActorRef =
    system.actorOf(AccountActor.props, "account")

  lazy val routes = accountRoutes

  "AccountRoutes" should {

    "be able to open accounts (POST /accounts)" in {
      val account = Account("1", "client1")
      val accountEntity = Marshal(account).to[MessageEntity].futureValue

      val request = Post("/accounts").withEntity(accountEntity)

      request ~> routes ~> check {
        status should ===(StatusCodes.Created)

        contentType should ===(ContentTypes.`application/json`)

        entityAs[String] should ===("""{"message":"Account created successfully! Account number : 1"}""")
      }
    }
  }
}
