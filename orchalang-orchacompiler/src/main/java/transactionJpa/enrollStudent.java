package transactionJpa;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class enrollStudent implements EnrollStudent {
    transactionJPAApplication.StudentService student1;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    /*public StudentDomain enroll(StudentDomain student) throws Exception {
        System.out.print("enrollStudent receives: " + student);
        student.setFirstName(student.getFirstName());
        if(student.getAge() > 30){
            System.out.println(" and throws an exception.");
            throw new Exception();
        }
        System.out.println(" and returns: " + student);
        return student;
    }*/

      public StudentDomain enroll(StudentDomain student) throws Exception {
        System.out.print("enroll receives: " + student);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.joinTransaction();
        StudentDomain studentDomain = new StudentDomain("Ben", 25);
        entityManager.persist(studentDomain);
        System.out.println(" and returns: " + student);
        return student;
    }

    /*
        public void manyStudentsInValideTransaction(){
        StudentDomain morgane = new StudentDomain("A", 20);		// no exception => A is commited in the database
        school.add(morgane);
        System.out.println("\nmanyStudentsInValideTransaction first student added\n");
        StudentDomain loic = new StudentDomain("B", 30);			// exception but new transaction => B is rolled back in the database
        school.add(loic);
    }
     */
}
