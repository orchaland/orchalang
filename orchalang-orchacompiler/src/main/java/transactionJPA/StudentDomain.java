package transactionJPA;

import javax.persistence.*;

@Entity(name = "Student")
@Table(name = "Student")
public class StudentDomain {

    public StudentDomain(String firstName, int age) {
        this.firstName = firstName;
        this.age = age;
    }

    public StudentDomain(){
    }

    @Id
    @Column(name = "rollNumber")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long rollNumber;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "age")
    private int age;

    public Long getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(Long rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "StudentDomain{" +
                "rollNumber=" + rollNumber +
                ", firstName='" + firstName + '\'' +
                ", age=" + age +
                '}';
    }
}