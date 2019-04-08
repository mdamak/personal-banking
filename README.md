# personal-banking

./sbt

restart

# CREATE ACCOUNT


curl -H "Content-type: application/json" -X POST -d '{"no":"2", "clientName":"mouna","balance":{"balance":0}}' http://localhost:8080/accounts
