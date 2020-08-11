package com.example.mongodb

import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway

class InfrastructureConfiguration {
    @MessagingGateway
    interface StudentService {
        @Gateway(requestChannel = "studentDatabaseFlow.input")
        fun student(student: StudentDomain?)
    }
}