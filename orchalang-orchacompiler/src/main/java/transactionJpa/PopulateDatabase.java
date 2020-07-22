package transactionJpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;

@Component
public class PopulateDatabase {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private DataSource dataSource;

    @Transactional
    public void saveStudent(StudentDomain student) throws Exception {
        System.out.print("saveStudent receives: " + student);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();
        entityManager.merge(student);
        System.out.println(" and save it.");
    }
//enrollStudent with student
//when "enrollStudent terminates"
//send  enrollStudent.result to studentBase
    /*	send student to studentDataBase		// transactional by default as soon as the database is transactional
	administrativeEnrollment student
	courseEnrollment student
	send student to welcomeFile*/

    public StudentDomain enrollStudent(StudentDomain student) throws Exception {
        System.out.print("enrollStudent receives: " + student);
      // student.setFirstName(student.getFirstName() + "-fileTransaction");
        if(student.getAge() > 30){
            System.out.println(" and throws an exception.");
            throw new Exception();
        }
        System.out.println(" and returns: " + student);
        return student;
    }

    public List<?> readDatabase(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
        List<?> results = jdbcTemplate.queryForList("Select * from Student");
        return results;
    }
}
