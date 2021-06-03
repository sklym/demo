package com.example.demo.service

import org.springframework.beans.factory.annotation.Autowired
import com.example.demo.repository.RecordRepository
import reactor.core.publisher.Flux
import com.example.demo.data.TestRecord
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@Service
class RecordService {
    @Autowired
    private val recordRepository: RecordRepository? = null

    fun all(): Flux<TestRecord?> {
        return recordRepository!!.findAll()
    }

    fun addTestRecords(list: List<TestRecord?>) {
        recordRepository!!.saveAll(list).subscribe()
    }
}