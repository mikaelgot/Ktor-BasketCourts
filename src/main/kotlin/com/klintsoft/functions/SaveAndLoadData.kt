package com.klintsoft.functions

import com.klintsoft.data.model.BasketCourt
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream

fun saveCourts(courtList: List<BasketCourt>) {
    val courtListSerializer = ListSerializer(BasketCourt.serializer())
    val jsonCourtList = Json.encodeToString(serializer = courtListSerializer, value = courtList)
    val file = File("build/resources/main/basketCourtData/courtList.json")
    file.writeText(jsonCourtList)
}

fun readCourts(): List<BasketCourt> {
    val courtListSerializer = ListSerializer(BasketCourt.serializer())
    val file = File("build/resources/main/basketCourtData/courtList.json")
    val jsonCourtList = file.readText()
    val courtList = Json.decodeFromString(courtListSerializer, jsonCourtList)
    return courtList
}