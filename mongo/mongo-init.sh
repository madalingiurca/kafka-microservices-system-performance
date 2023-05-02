set -e

mongosh <<EOF
use admin
db.auth("admin", "admin")

use orders
db.createUser({
  user: "ordersWorker",
  pwd: "ordersPass",
  roles: [ { role: "readWrite", db: "orders" } ]
})

use payments
db.createUser({
  user: "paymentsWorker",
  pwd: "paymentsPass",
  roles: [ { role: "readWrite", db: "payments" } ]
})

use tracking
db.createUser({
  user: "trackingWorker",
  pwd: "trackingPass",
  roles: [ { role: "readWrite", db: "tracking" } ]
})

use kafkaOrders
db.createUser({
  user: "kafkaWorker",
  pwd: "kafkaPass",
  roles: [ { role: "readWrite", db: "kafkaOrders" } ]
})
EOF