package com.xliiicxiv.scrapper.extension

import android.content.Context
import android.net.Uri
import com.xliiicxiv.scrapper.dataclass.DptResult
import com.xliiicxiv.scrapper.dataclass.LasikResult
import com.xliiicxiv.scrapper.dataclass.SiipResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream

fun getDataForSiip(
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

fun getDataForDpt(
    context: Context,
    xlsxUri: Uri
): Flow<DptResult> {
    return flow {
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(xlsxUri)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            val formatter = DataFormatter()

            for (row in sheet) {
                if (row.rowNum == 0) {
                    continue
                }

                val kpjNumber = formatter.formatCellValue(
                    row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val nikNumber = formatter.formatCellValue(
                    row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val fullName = formatter.formatCellValue(
                    row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val birthDate = formatter.formatCellValue(
                    row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val email = formatter.formatCellValue(
                    row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                if (kpjNumber.isNotEmpty() && nikNumber.isNotEmpty()) {
                    emit(
                        DptResult(
                            kpjNumber = kpjNumber,
                            nikNumber = nikNumber,
                            fullName = fullName,
                            birthDate = birthDate,
                            email = email,
                            regencyName = "",
                            subdistrictName = "",
                            wardName = "",
                        )
                    )
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

fun getDataForLasik(
    context: Context,
    xlsxUri: Uri
): Flow<LasikResult> {
    return flow {
        var inputStream: InputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(xlsxUri)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            val formatter = DataFormatter()

            for (row in sheet) {
                if (row.rowNum == 0) {
                    continue
                }

                val kpjNumber = formatter.formatCellValue(
                    row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val nikNumber = formatter.formatCellValue(
                    row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val fullName = formatter.formatCellValue(
                    row.getCell(2, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val birthDate = formatter.formatCellValue(
                    row.getCell(3, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val email = formatter.formatCellValue(
                    row.getCell(4, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val regencyName = formatter.formatCellValue(
                    row.getCell(5, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val subdistrictName = formatter.formatCellValue(
                    row.getCell(6, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                val wardName = formatter.formatCellValue(
                    row.getCell(7, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)
                ).trim()

                if (kpjNumber.isNotEmpty() && nikNumber.isNotEmpty()) {
                    emit(
                        LasikResult(
                            kpjNumber = kpjNumber,
                            nikNumber = nikNumber,
                            fullName = fullName,
                            birthDate = birthDate,
                            email = email,
                            regencyName = regencyName,
                            subdistrictName = subdistrictName,
                            wardName = wardName,
                            lasikResult = ""
                        )
                    )
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
    val sheet = workbook.createSheet("SIIP Result")

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

fun exportToExcelDpt(
    context: Context,
    path: String,
    fileName: String,
    dptResult: List<DptResult>
) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("DPT Result")

    val maxColumnWidths = mutableMapOf(
        0 to "KPJ Number".length,
        1 to "NIK Number".length,
        2 to "Full Name".length,
        3 to "Birth Date".length,
        4 to "E-Mail".length,
        5 to "Kabupaten Name".length,
        6 to "Kecamatan Name".length,
        7 to "Kelurahan Name".length
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
    headerRow.createCell(5).setCellValue("Kabupaten Name")
    headerRow.createCell(6).setCellValue("Kecamatan Name")
    headerRow.createCell(7).setCellValue("Kelurahan Name")

    dptResult.forEachIndexed { index, result ->
        val dataRow = sheet.createRow(index + 1)

        dataRow.createCell(0).setCellValue(result.kpjNumber)
        dataRow.createCell(1).setCellValue(result.nikNumber)
        dataRow.createCell(2).setCellValue(result.fullName)
        dataRow.createCell(3).setCellValue(result.birthDate)
        dataRow.createCell(4).setCellValue(result.email)
        dataRow.createCell(5).setCellValue(result.regencyName)
        dataRow.createCell(6).setCellValue(result.subdistrictName)
        dataRow.createCell(7).setCellValue(result.wardName)

        updateMaxWidth(0, result.kpjNumber)
        updateMaxWidth(1, result.nikNumber)
        updateMaxWidth(2, result.fullName)
        updateMaxWidth(3, result.birthDate)
        updateMaxWidth(4, result.email)
        updateMaxWidth(5, result.regencyName)
        updateMaxWidth(6, result.subdistrictName)
        updateMaxWidth(7, result.wardName)
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

fun exportToExcelLasik(
    context: Context,
    path: String,
    fileName: String,
    lasikResult: List<LasikResult>
) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("DPT Result")

    val maxColumnWidths = mutableMapOf(
        0 to "KPJ Number".length,
        1 to "NIK Number".length,
        2 to "Full Name".length,
        3 to "Birth Date".length,
        4 to "E-Mail".length,
        5 to "Kabupaten Name".length,
        6 to "Kecamatan Name".length,
        7 to "Kelurahan Name".length,
        8 to "Lasik Result".length
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
    headerRow.createCell(5).setCellValue("Kabupaten Name")
    headerRow.createCell(6).setCellValue("Kecamatan Name")
    headerRow.createCell(7).setCellValue("Kelurahan Name")
    headerRow.createCell(8).setCellValue("Lasik Result")

    lasikResult.forEachIndexed { index, result ->
        val dataRow = sheet.createRow(index + 1)

        dataRow.createCell(0).setCellValue(result.kpjNumber)
        dataRow.createCell(1).setCellValue(result.nikNumber)
        dataRow.createCell(2).setCellValue(result.fullName)
        dataRow.createCell(3).setCellValue(result.birthDate)
        dataRow.createCell(4).setCellValue(result.email)
        dataRow.createCell(5).setCellValue(result.regencyName)
        dataRow.createCell(6).setCellValue(result.subdistrictName)
        dataRow.createCell(7).setCellValue(result.wardName)
        dataRow.createCell(8).setCellValue(result.lasikResult)

        updateMaxWidth(0, result.kpjNumber)
        updateMaxWidth(1, result.nikNumber)
        updateMaxWidth(2, result.fullName)
        updateMaxWidth(3, result.birthDate)
        updateMaxWidth(4, result.email)
        updateMaxWidth(5, result.regencyName)
        updateMaxWidth(6, result.subdistrictName)
        updateMaxWidth(7, result.wardName)
        updateMaxWidth(8, result.lasikResult)
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