package com.xliiicxiv.scrapper.extension

fun removeDoubleQuote(string: String) : String {
    val removedQuote = string.replace("\"", "")
    return removedQuote
}