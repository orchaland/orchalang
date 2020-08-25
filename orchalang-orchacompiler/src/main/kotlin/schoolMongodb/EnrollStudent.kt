package schoolMongodb

class EnrollStudent {
    fun enroll(student: StudentDomain): StudentDomain {
        student.rollNumber = 1
        println("enroll returns: $student")
        return student
    }
    fun get(): StudentDomain {
        println("ok")
        return StudentDomain("Marwa", 40)
    }
}