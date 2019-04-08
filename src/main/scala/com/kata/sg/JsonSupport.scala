package com.kata.sg

import com.kata.sg.UserRegistryActor.ActionPerformed
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.kata.sg.actor.AccountActor.AccountOpened
import com.kata.sg.actor.OperationActor.OperationPerformed
import com.kata.sg.model._
import spray.json._
trait JsonSupport extends SprayJsonSupport with DateMarshalling {
  import DefaultJsonProtocol._

  implicit val opTypeJsonFormat = new JsonFormat[OpType] {
    def write(obj: OpType) = JsString(obj match {
      case Withdrawal => "Withdrawal"
      case Deposit => "Deposit"
    })
    def read(json: JsValue): OpType =
      json match {
        case JsString("Withdrawal") => Withdrawal
        case JsString("Deposit") => Deposit
      }
  }

  implicit val balanceJsonFormat = jsonFormat1(Balance)
  implicit val accountJsonFormat = jsonFormat3(Account)
  implicit val accountsJsonFormat = jsonFormat1(Accounts)
  implicit val accountOpenedJsonFormat = jsonFormat1(AccountOpened)

  implicit val amountJsonFormat = jsonFormat1(Amount)
  implicit val operationJsonFormat = jsonFormat5(Operation)
  implicit val operationPerformedJsonFormat = jsonFormat1(OperationPerformed)
  implicit val historyJsonFormat = jsonFormat1(History)

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)
  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

}
