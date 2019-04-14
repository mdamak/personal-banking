package com.kata.sg.actor

import akka.actor.{ Actor, Props }
import akka.event.LoggingReceive
import com.kata.sg.model.Balance
import com.kata.sg.service.AccountService

class AccountActor extends Actor {

  import AccountActor._

  private final val service = AccountService

  override def receive: Receive = LoggingReceive {
    case OpenAccount(no: String, clientName: String, balance: Balance) =>
      sender() ! service.openAccount(no, clientName, balance)

    case GetAccount(message: String) =>
      sender() ! service.getAccount(message)

    case GetAccounts() =>
      sender() ! service.getAccounts()
  }
}

object AccountActor {
  def props: Props = Props[AccountActor]

  final case class OpenAccount(no: String, clientName: String, balance: Balance = Balance())

  final case class GetAccount(no: String)

  final case class GetAccounts()

  final case class AccountOpened(message: String)

}