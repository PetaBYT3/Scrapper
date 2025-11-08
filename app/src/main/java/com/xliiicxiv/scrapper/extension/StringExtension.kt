package com.xliiicxiv.scrapper.extension

fun removeDoubleQuote(string: String) : String {
    val removedQuote = string.replace("\"", "")
    return removedQuote
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