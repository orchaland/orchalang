package orcha.lang.configuration

class DatabaseAdapterMongodb (
    val adapter: Adapter = Adapter.Database,
    val connection: DatabaseConnectionMongodb = DatabaseConnectionMongodb())
: ConfigurableProperties()