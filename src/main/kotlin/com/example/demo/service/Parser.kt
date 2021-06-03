package com.example.demo.service

import com.example.demo.data.TestRecord
import org.apache.poi.openxml4j.exceptions.InvalidFormatException
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import reactor.core.CoreSubscriber
import reactor.core.publisher.Flux
import java.io.IOException
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat


class Parser {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    fun parse(`in`: InputStream?): Flux<TestRecord?> {
        return object : Flux<TestRecord?>() {
            override fun subscribe(s: CoreSubscriber<in TestRecord?>) {
                try {
                    var wb: XSSFWorkbook? = null
                    try {

                        //    in = getClass().getClassLoader().getResourceAsStream(name);
                        val pkg = OPCPackage.open(`in`)
                        wb = XSSFWorkbook(pkg)
                        //   wb = new XSSFWorkbook(in);
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: InvalidFormatException) {
                        e.printStackTrace()
                    }
                    val sheet: Sheet = wb!!.getSheetAt(0)
                    val it: Iterator<Row> = sheet.iterator()
                    val firstRow = it.next()
                    val fullNameCellIndex = getIndex(COLUMN_1, firstRow)
                    val amountCellIndex = getIndex(COLUMN_2, firstRow)
                    val dateCelIndex = getIndex(COLUMN_3, firstRow)
                    val genderIndex = getIndex(COLUMN_4, firstRow)
                    while (it.hasNext()) {
                        val result = ""
                        val row = it.next()
                        val cells: Iterator<Cell> = row.iterator()
                        val testRecord = TestRecord(
                            getValue(row.getCell(fullNameCellIndex)) as String,
                            getValue(row.getCell(amountCellIndex)) as Double,
                            dateFormat.parse(getValue(row.getCell(dateCelIndex)) as String),
                            getValue(row.getCell(genderIndex)) as String)
                        s.onNext(testRecord)
                    }
                    s.onComplete()
                } catch (ex: ParseException) {
                    s.onError(ex)
                }
            }
        }
    }

    private fun getIndex(cellName: String, row: Row): Int {
        val cells: Iterator<Cell> = row.iterator()
        var i = 0
        while (cells.hasNext()) {
            val cell = cells.next()
            val columnName = cell.stringCellValue.trim { it <= ' ' }
            if (cellName.equals(columnName, ignoreCase = true)) {
                return i
            }
            i++
        }
        return -1
    }

    private fun getValue(cell: Cell): Any {
        var result: Any = ""
        val cellType = cell.cellType
        when (cellType) {
            CellType.STRING -> result = cell.stringCellValue
            CellType.NUMERIC -> result = cell.numericCellValue
            CellType.FORMULA -> result = cell.numericCellValue
            else -> {
            }
        }
        return result
    }

    companion object {
        private const val COLUMN_1 = "Column 1"
        private const val COLUMN_2 = "Column 2"
        private const val COLUMN_3 = "Random Column XYZ $"
        private const val COLUMN_4 = "Random Test"
    }
}