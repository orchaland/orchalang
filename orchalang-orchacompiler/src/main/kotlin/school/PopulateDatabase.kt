package school

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource


@Component
class PopulateDatabase {
    @Autowired
    private val entityManagerFactory: EntityManagerFactory? = null

    @Autowired
    private val dataSource: DataSource? = null

    @Transactional
    @Throws(Exception::class)
    fun saveStudent(student: StudentDomain) {
        print("saveStudent receives: $student")
        val entityManager = entityManagerFactory!!.createEntityManager()
        entityManager.joinTransaction()
        entityManager.merge<Any>(student)
        println(" and save it.")
    }


    fun readDatabase(): List<*> {
        val jdbcTemplate = JdbcTemplate(dataSource!!)
        return jdbcTemplate.queryForList("Select * from Student")
    }
}
