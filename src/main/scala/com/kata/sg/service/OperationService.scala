package com.kata.sg.service

import com.kata.sg.actor.OperationActor.OperationPerformed
import com.kata.sg.model.{ Deposit, History, Operation, Withdrawal }
import com.kata.sg.repository.OperationRepositoryInMemory

trait OperationService {

  def addOperation(operation: Operation): Option[OperationPerformed]

  def getHistory(accountNo: String): History
}

object OperationService extends OperationService {

  private final val repo = OperationRepositoryInMemory // TODO later use injection to inject the right repo
  private final val accountService = AccountService

  override def addOperation(operation: Operation): Option[OperationPerformed] = {
    (operation.opType match {
      case Withdrawal => accountService.debitAccount(operation.accountNo, operation.amount)
      case Deposit => accountService.creditAccount(operation.accountNo, operation.amount)
    }).flatMap(_ => repo.add(operation))
      .map(_ => OperationPerformed("Operation performed successfully!"))
  }

  override def getHistory(accountNo: String): History = History(List(Operation("1", "2", Withdrawal)))

}

