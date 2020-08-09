package orcha.lang.configuration

data class DatabaseAdapter (
        val adapter: Adapter = Adapter.Database,
        val connection: DatabaseConnection = DatabaseConnection(),
        val hibernateConfig: DatabaseHibernateConfig = DatabaseHibernateConfig()
) : ConfigurableProperties()