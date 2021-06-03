package com.example.demo.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class TestRecord(var fullName: String, var amount: Double, var dateOfBirth: Date, var gender: String) {
    @Id
    var id: String? = null

}