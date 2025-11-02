package com.xliiicxiv.scrapper.extension

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.InputStream

suspend fun parseXlsxFile(
    context: Context,
    xlsxUri: Uri
) : List<String> {

    return withContext(Dispatchers.IO) {
        val stringList = mutableListOf<String>()
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(xlsxUri)

            val workBook = WorkbookFactory.create(inputStream)
            val sheet = workBook.getSheetAt(0)
            val formatter = DataFormatter()

            for (row in sheet) {
                val cell = row.getCell(0)
                val cellValue = formatter.formatCellValue(cell).trim()

                if (cellValue.isNotEmpty()) {
                    stringList.add(cellValue)
                }
            }

            workBook.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }

        stringList
    }
}

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
                    // 2. 'emit' data satu per satu, BUKAN 'list.add'
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