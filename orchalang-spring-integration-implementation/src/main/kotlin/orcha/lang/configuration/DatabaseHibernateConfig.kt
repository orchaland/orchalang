package orcha.lang.configuration

data class DatabaseHibernateConfig(
        val dialect: String = "org.hibernate.dialect.H2Dialect",
        val hbm2ddlAuto: String = "create-drop",
        val ejbNamingStrategy: String = "org.hibernate.cfg.ImprovedNamingStrategy",
        val showSql: Boolean = false,
        val formatSql: Boolean = true
)