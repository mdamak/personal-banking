package com.kata.sg.repository

import scala.collection.mutable.Map
import com.kata.sg.model.{Account, Accounts, Balance}

import scala.util.{Failure, Success, Try}

trait AccountRepository {
  def open(no: String, clientName: String, balance: Balance = Balance()): Option[Account]

  def get(no: String): Option[Account]

  def update(noAccount: String, account: Account): Try[Account]

  def getAll(): Accounts
}

trait AccountRepositoryInMemory extends AccountRepository {
  private lazy val accountsRepo = Map.empty[String, Account]

  override def open(no: String, clientName: String, balance: Balance): Option[Account] = {
    val account = Account(no, clientName, balance)
    accountsRepo += ((no, account))
    Some(account)
  }

  override def get(no: String): Option[Account] =
    accountsRepo get no

  override def getAll(): Accounts =
    Accounts(accountsRepo.values.toSet)

  override def update(noAccount: String, account: Account): Try[Account] =
    accountsRepo.put(noAccount, account)
      .map(Success(_))
      .getOrElse(Failure(new Exception("Cannot update account")))

}

object AccountRepositoryInMemory extends AccountRepositoryInMemory

