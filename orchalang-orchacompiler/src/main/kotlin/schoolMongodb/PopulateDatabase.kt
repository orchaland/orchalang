package schoolMongodb

//import com.mongodb.reactivestreams.client.MongoClients

import com.mongodb.client.MongoClients
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux


@Component
class PopulateDatabase( var mongoOps: MongoOperations = MongoTemplate(MongoClients.create(), "database")) {

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

  fun readDatabase(): List<StudentDomain> {
      return mongoOps.find(Query(), StudentDomain::class.java)

    }

    fun  delatStudent(student: StudentDomain) {
        mongoOps.dropCollection("student");
    }
}