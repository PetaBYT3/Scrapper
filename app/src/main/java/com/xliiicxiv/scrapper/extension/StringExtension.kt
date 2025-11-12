package com.xliiicxiv.scrapper.extension

fun removeDoubleQuote(string: String) : String {
    val removedQuote = string.replace("\"", "")
    return removedQuote
}

fun getFullName(string: String) : String {
    val fullName = string.replace("Nama Pemilih\\n", "")
    return fullName
}

fun getRegencyName(string: String) : String {
    val regencyName = string.replace("Kabupaten", "")
    return regencyName
}

fun getSubdistrictName(string: String) : String {
    val subdistrictName = string.replace("Kecamatan", "")
    return subdistrictName
}

fun getWardName(string: String) : String {
    val wardName = string.replace("Kelurahan", "")
    return wardName
}