package school

class EnrollStudent {
    fun enroll(student: StudentDomain): StudentDomain {
        student.rollNumber = 1
        println("enroll returns: $student")
        return student
    }
}