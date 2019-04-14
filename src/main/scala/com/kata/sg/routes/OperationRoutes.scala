package com.kata.sg.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.{get, post}
import akka.http.scaladsl.server.directives.PathDirectives.path
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.kata.sg.JsonSupport
import com.kata.sg.actor.OperationActor.{AddOperation, GetHistory, OperationPerformed}
import com.kata.sg.model.{History, Operation}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try

trait OperationRoutes extends JsonSupport {

  implicit def system: ActorSystem

  lazy val logOperation = Logging(system, classOf[OperationRoutes])

  def operationActor: ActorRef

  implicit lazy val timeoutOperation = Timeout(5.seconds) // TODO obtain the timeout from the system's configuration

  lazy val operationRoutes: Route =
    pathPrefix("operations") {
      concat(
        pathEnd {
          post {
            entity(as[Operation]) { operation =>
              val operationPerformed: Future[Try[OperationPerformed]] =
                (operationActor ? AddOperation(operation)).mapTo[Try[OperationPerformed]]
              onSuccess(operationPerformed) { performed =>
                logOperation.info(performed.map(_.message).getOrElse("Cannot perform operation")) //TODO improve error traitement
                complete((StatusCodes.Created, performed))
              }
            }
          }
        },
        path(Segment) { accountNo =>
          get {
            val operations: Future[History] =
              (operationActor ? GetHistory(accountNo)).mapTo[History]
            rejectEmptyResponse {
              complete(operations)
            }
          }
        })
    }
}
