package com.kata.sg.actor

import akka.actor.{ Actor, Props }
import akka.event.LoggingReceive
import com.kata.sg.model.Operation
import com.kata.sg.service.OperationService

class OperationActor extends Actor {

  import OperationActor._

  private final val service = OperationService

  override def receive: Receive = LoggingReceive {
    case AddOperation(operation) =>
      sender() ! service.addOperation(operation)

    case GetHistory(accountNo: String) =>
      sender() ! service.getHistory(accountNo)

  }
}

object OperationActor {
  def props: Props = Props[OperationActor]

  final case class AddOperation(operation: Operation)

  final case class GetHistory(accountNo: String)

  final case class OperationPerformed(message: String)

}

