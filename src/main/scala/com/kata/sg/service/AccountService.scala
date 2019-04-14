package com.kata.sg.service

import com.kata.sg.actor.AccountActor.AccountOpened
import com.kata.sg.model.{Account, Accounts, Amount, Balance}
import com.kata.sg.repository.AccountRepositoryInMemory

import scala.util.{Failure, Success, Try}

trait AccountService {

  def getAccount(no: String): Option[Account]

  def getAccounts(): Accounts

  def openAccount(no: String, clientName: String, balance: Balance = Balance()): Option[AccountOpened]

  def debitAccount(no: String, amount: Amount): Try[Account]

  def creditAccount(no: String, amount: Amount): Try[Account]
}

object AccountService extends AccountService {

  private final val repo = AccountRepositoryInMemory // TODO  use injection to inject the right repo

  override def getAccount(no: String): Option[Account] = repo.get(no)

  override def getAccounts(): Accounts = repo.getAll()

  override def openAccount(no: String, clientName: String, balance: Balance): Option[AccountOpened] =
    repo.open(no, clientName, balance).map(account => AccountOpened("Account created successfully! Account number : " + account.no))

  override def debitAccount(no: String, amount: Amount): Try[Account] = {
    (repo.get(no) match {
      case None => Failure(new Exception("Account not found"))
      case Some(a) => if (a.balance.balance < amount.amount)
        Failure(new Exception("Insufficient balance"))
      else Success(a.copy(balance = Balance(a.balance.balance - amount.amount)))
    }).flatMap(a => repo.update(no, a))
  }

  override def creditAccount(no: String, amount: Amount): Try[Account] =
    repo.get(no)
      .map(a => Success(a.copy(balance = Balance(a.balance.balance + amount.amount))))
      .getOrElse(Failure(new Exception("Account not found")))
      .flatMap(a => repo.update(no, a))

}
