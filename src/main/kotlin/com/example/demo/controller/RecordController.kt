package com.example.demo.controller


import com.example.demo.data.TestRecord
import com.example.demo.service.Parser
import com.example.demo.service.RecordService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.util.StopWatch
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.IOException
import java.io.InputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

@RestController
class RecordController {
    val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private val recordService: RecordService? = null
    @GetMapping("records")
    fun test(): Flux<TestRecord?> {
        return recordService!!.all()
    }

    @PostMapping(value = ["upload"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Throws(
        IOException::class
    )
    fun uploadAndInsert(@RequestPart("file") filePart: Mono<FilePart>): Mono<Void> {
        logger.info("start")
        //TODO: check mime-type
        val stopWatch = StopWatch()
        stopWatch.start()

        return filePart.doOnNext { fp: FilePart ->
            logger.info("st: {}", fp.filename())
            try {
                val `in` = getInputStreamFromFluxDataBuffer(fp.content())
                logger.info("stream : {}", `in`)
                val parser = Parser()
                parser.parse(`in`)
                    .buffer(50).map { testRecords: List<TestRecord?>? ->
                        recordService!!.addTestRecords(testRecords!!)
                        1
                    }
                    .subscribe()
                stopWatch.stop()
                logger.info("Test: {} sec", stopWatch.totalTimeSeconds)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.then()


        //  return ResponseEntity.ok().build();
    }

    @Throws(IOException::class)
    fun getInputStreamFromFluxDataBuffer(data: Flux<DataBuffer?>?): InputStream {
        val osPipe = PipedOutputStream()
        val isPipe = PipedInputStream(osPipe)
        DataBufferUtils.write(data!!, osPipe)
            .subscribeOn(Schedulers.elastic())
            .doOnComplete {
                try {
                    osPipe.close()
                } catch (ignored: IOException) {
                }
            }
            .subscribe(DataBufferUtils.releaseConsumer())
        return isPipe
    }


}