package com.kata.sg.service

import com.kata.sg.actor.OperationActor.OperationPerformed
import com.kata.sg.model.{Deposit, History, Operation, Withdrawal}
import com.kata.sg.repository.OperationRepositoryInMemory

import scala.util.Try

trait OperationService {

  def addOperation(operation: Operation): Try[OperationPerformed]

  def getHistory(accountNo: String): History
}

object OperationService extends OperationService {

  private final val repo = OperationRepositoryInMemory // TODO later use injection to inject the right repo
  private final val accountService = AccountService

  override def addOperation(operation: Operation): Try[OperationPerformed] = {
    (operation.opType match {
      case Withdrawal => accountService.debitAccount(operation.accountNo, operation.amount)
      case Deposit => accountService.creditAccount(operation.accountNo, operation.amount)
    }).map(_ => repo.add(operation))
      .map(_ => OperationPerformed("Operation performed successfully!"))
  }

  override def getHistory(accountNo: String): History = History(repo.getAllForAccount(accountNo))

}

