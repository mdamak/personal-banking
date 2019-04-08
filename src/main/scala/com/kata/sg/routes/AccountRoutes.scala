package com.kata.sg.routes

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{ get, post }
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.kata.sg.JsonSupport
import com.kata.sg.actor.AccountActor.{ AccountOpened, GetAccount, OpenAccount }
import com.kata.sg.model.Account

import scala.concurrent.Future
import scala.concurrent.duration._

trait AccountRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val logAccount = Logging(system, classOf[AccountRoutes])

  def accountActor: ActorRef

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  lazy val accountRoutes: Route =
    pathPrefix("accounts") {
      concat(
        pathEnd {
          post {
            entity(as[Account]) { account =>
              val accountOpened: Future[Option[AccountOpened]] =
                (accountActor ? OpenAccount(account.no, account.clientName, account.balance)).mapTo[Option[AccountOpened]]
              onSuccess(accountOpened) { opened =>
                logAccount.info(opened.map(_.message).getOrElse("Cannot open account"))
                complete((StatusCodes.Created, opened))
              }
            }
          }
        },
        path(Segment) { no =>
          get {
            val maybeAccount: Future[Option[Account]] =
              (accountActor ? GetAccount(no)).mapTo[Option[Account]]
            rejectEmptyResponse {
              complete(maybeAccount)
            }
          }
        })
    }
}
