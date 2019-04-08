package com.kata.sg.model

final case class Balance(balance: BigDecimal = 0)

final case class Account(no: String, clientName: String, balance: Balance = Balance())

final case class Accounts(accounts: Set[Account])
