package mongodb

//import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoAdminOperations
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

@Component
//class PopulateDatabase(val mongoOps: ReactiveMongoTemplate = ReactiveMongoTemplate(MongoClients.create(), "test")) {
class PopulateDatabase(  val mongoOps:ReactiveMongoTemplate) {



    fun saveStudent(student: StudentDomain) {
        mongoOps.insert(student)
    }

    @Throws(Exception::class)
    fun enrollStudent(student: StudentDomain): StudentDomain {
        print("enrollStudent receives: $student")
        student.rollNumber = 1
        println(" and returns: $student")
        return student
    }

    fun readDatabase(): Flux<StudentDomain> {
        val query = Query()
       // val jdbcTemplate = JdbcTemplate(dataSource!!)
       // return jdbcTemplate.queryForList("Select * from Student")
      return  mongoOps.find(query, StudentDomain::class.java)

    }
}