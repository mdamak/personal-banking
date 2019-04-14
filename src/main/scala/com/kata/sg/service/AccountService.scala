package com.kata.sg.service

import com.kata.sg.actor.AccountActor.AccountOpened
import com.kata.sg.model.{Account, Accounts, Amount, Balance}
import com.kata.sg.repository.AccountRepositoryInMemory

trait AccountService {

  def getAccount(no: String): Option[Account]

  def getAccounts(): Accounts

  def openAccount(no: String, clientName: String, balance: Balance = Balance()): Option[AccountOpened]

  def debitAccount(no: String, amount: Amount): Option[Account]

  def creditAccount(no: String, amount: Amount): Option[Account]
}

object AccountService extends AccountService {

  private final val repo = AccountRepositoryInMemory // TODO  use injection to inject the right repo

  override def getAccount(no: String): Option[Account] = repo.get(no)

  override def getAccounts(): Accounts = repo.getAll()

  override def openAccount(no: String, clientName: String, balance: Balance): Option[AccountOpened] =
    repo.open(no, clientName, balance).map(account => AccountOpened("Account created successfully! Account number : " + account.no))

  override def debitAccount(no: String, amount: Amount): Option[Account] =
    repo.get(no).map(account => Account(account.no, account.clientName, Balance(account.balance.balance - amount.amount)))

  override def creditAccount(no: String, amount: Amount): Option[Account] =
    repo.get(no).map(account => Account(account.no, account.clientName, Balance(account.balance.balance + amount.amount)))

}
