package orcha.lang.configuration

data class DatabaseConnection(val driver: String = "org.h2.Driver",
                              val url: String ="jdbc:h2:mem:datajpa",
                              val username: String = "sa",
                              val password: String = "",
                              val entityScanPackage: String = "com.example.jpa") {
}