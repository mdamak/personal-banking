package com.kata.sg.model

import java.util.Date

case class Amount(amount: BigDecimal = 0)

sealed trait OpType

case object Withdrawal extends OpType

case object Deposit extends OpType

final case class Operation(no: String, accountNo: String, opType: OpType, date: Date = new Date(), amount: Amount = Amount())

final case class History(operations: List[Operation])
