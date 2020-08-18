package mongodb

class EnrollStudent {
    fun enroll(student: StudentDomain): StudentDomain {
        student.rollNumber = 1
        println("enroll returns: $student")
        return student
    }
    fun get(): StudentDomain {
        println("ooooookkkkkkkk")
        return StudentDomain(firstName = "Lineda", age = 40)
    }
}