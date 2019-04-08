package com.kata.sg.repository

import scala.collection.mutable.Map
import com.kata.sg.model.{ Account, Balance }

trait AccountRepository {
  def open(no: String, clientName: String, balance: Balance = Balance()): Option[Account]

  def get(no: String): Option[Account]

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

}

object AccountRepositoryInMemory extends AccountRepositoryInMemory

