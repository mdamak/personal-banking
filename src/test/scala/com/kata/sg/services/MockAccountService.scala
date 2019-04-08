package com.kata.sg.services

import com.kata.sg.actor.AccountActor
import com.kata.sg.model.{Account, Amount, Balance}
import com.kata.sg.service.AccountService

trait MockAccountService extends AccountService {

  override def getAccount(no: String): Option[Account] = Some(Account("1", "clientName"))

  override def openAccount(no: String, clientName: String, balance: Balance): Option[AccountActor.AccountOpened] = ???

  override def creditAccount(no: String, amount: Amount): Option[Account] = ???

  override def debitAccount(no: String, amount: Amount): Option[Account] = ???
}
