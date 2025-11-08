package com.xliiicxiv.scrapper.extension

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentTime() : String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")

    return currentDateTime.format(formatter)
}