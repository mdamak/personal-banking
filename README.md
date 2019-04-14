# personal-banking

`./sbt`

`reStart`

# Create account

`curl -H "Content-type: application/json" -X POST -d '{"no":"2", "clientName":"client","balance":{"balance":0}}' http://localhost:8080/accounts
`

# Get account

`curl http://localhost:8080/accounts/2`

# Get all accounts

`curl http://localhost:8080/accounts`

# Perform operation

`curl -H "Content-type: application/json" -X POST -d '{"accountNo":"2","amount":{"amount":0},"date":"2019-04-08T19:06:08.511+0200","no":"1","opType":"Withdrawal"}' http://localhost:8080/operations`

#Get operations

`curl http://localhost:8080/operations/2`