package com.example.demo.repository

import com.example.demo.data.TestRecord
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface RecordRepository : ReactiveMongoRepository<TestRecord?, Long?>