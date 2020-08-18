package mongodb

class StudentDomain {
    constructor(firstName: String?, age: Int) {
        this.firstName = firstName
        this.age = age
    }

    constructor(firstName: String?, age: Int, rollNumber: Long) {
        this.firstName = firstName
        this.age = age
        this.rollNumber = rollNumber
    }

    constructor() {}

    var rollNumber: Long? = null

    var firstName: String? = null

    var age = 0

    override fun toString(): String {
        return "StudentDomain{" +
                "rollNumber=" + rollNumber +
                ", firstName='" + firstName + '\'' +
                ", age=" + age +
                '}'
    }
}