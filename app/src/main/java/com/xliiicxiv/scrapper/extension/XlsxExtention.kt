package com.xliiicxiv.scrapper.extension

import android.content.Context
import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.SiipResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.io.InputStream

fun parseSingleColumnExcel(
    context: Context,
    xlsxUri: Uri
): Flow<String> {

    return flow {
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(xlsxUri)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            val formatter = DataFormatter()

            for (row in sheet) {
                val cell = row.getCell(0)
                val cellValue = formatter.formatCellValue(cell).trim()

                if (cellValue.isNotEmpty()) {
                    emit(cellValue)
                }
            }

            workbook.close()

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
    }.flowOn(Dispatchers.IO)
}

fun exportToExcelSiip(
    context: Context,
    path: String,
    fileName: String,
    siipResult: List<SiipResult>
) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Siip Result")

    val maxColumnWidths = mutableMapOf(
        0 to "KPJ Number".length,
        1 to "NIK Number".length,
        2 to "Full Name".length,
        3 to "Birth Date".length,
        4 to "E-Mail".length
    )

    fun updateMaxWidth(columnIndex: Int, text: String) {
        val currentMax = maxColumnWidths[columnIndex] ?: 0
        val newWidth = text.length
        if (newWidth > currentMax) {
            maxColumnWidths[columnIndex] = newWidth
        }
    }

    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("KPJ Number")
    headerRow.createCell(1).setCellValue("NIK Number")
    headerRow.createCell(2).setCellValue("Full Name")
    headerRow.createCell(3).setCellValue("Birth Date")
    headerRow.createCell(4).setCellValue("E-Mail")

    siipResult.forEachIndexed { index, result ->
        val dataRow = sheet.createRow(index + 1)

        dataRow.createCell(0).setCellValue(result.kpjNumber)
        dataRow.createCell(1).setCellValue(result.nikNumber)
        dataRow.createCell(2).setCellValue(result.fullName)
        dataRow.createCell(3).setCellValue(result.birthDate)
        dataRow.createCell(4).setCellValue(result.email)

        updateMaxWidth(0, result.kpjNumber)
        updateMaxWidth(1, result.nikNumber)
        updateMaxWidth(2, result.fullName)
        updateMaxWidth(3, result.birthDate)
        updateMaxWidth(4, result.email)
    }

    try {
        val outputStream = saveFile(
            context = context,
            path = path,
            fileName = fileName
        )

        outputStream?.use { stream ->
            workbook.write(stream)
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }

    workbook.close()
}