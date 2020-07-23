package transactionJpa;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class EnrollStudent {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public StudentDomain enrollStudent(StudentDomain student) throws Exception {
        System.out.print("enrollStudent receives: " + student);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();
        StudentDomain studentDomain = new StudentDomain("Ben", 25,5);
        entityManager.persist(studentDomain);		// attention même si merge est utilisé, si nouvelle tx => nouvelle insertion !
        System.out.println(" and returns: " + student);
        return student;
    }
}
