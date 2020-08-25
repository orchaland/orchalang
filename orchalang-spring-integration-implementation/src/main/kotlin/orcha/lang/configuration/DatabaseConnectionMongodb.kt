package orcha.lang.configuration

class DatabaseConnectionMongodb ( val mongoUrl: String = "mongo.db.url:mongodb://127.0.0.1",
                                  val mongoHost: String ="mongo.db.name:admin",
                                  val mongoPort: String = "mongo.db.port:27017}",
                                  val entityScanPackage: String = "com.example.mongodb"){
}