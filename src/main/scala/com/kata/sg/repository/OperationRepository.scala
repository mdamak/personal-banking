package com.kata.sg.repository

import com.kata.sg.model.Operation

import scala.collection.mutable.Map

trait OperationRepository {
  def add(operation: Operation): Option[Operation]

  def getAllForAccount(no: String): List[Operation]

}

trait OperationRepositoryInMemory extends OperationRepository {
  private lazy val operationsRepo = Map.empty[String, List[Operation]]

  override def add(operation: Operation): Option[Operation] = {
    val updatedOperations = operationsRepo.get(operation.accountNo)
      .map(ops => operation :: ops)
      .getOrElse(List(operation))
    operationsRepo.put(operation.accountNo, updatedOperations)
    Some(operation)
  }

  override def getAllForAccount(accountNo: String): List[Operation] =
    operationsRepo.getOrElse(accountNo, List.empty)

}

object OperationRepositoryInMemory extends OperationRepositoryInMemory

