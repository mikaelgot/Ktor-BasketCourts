package com.klintsoft.routes

import com.klintsoft.data.model.BasketCourt
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

private const val BASE_URL = "http://localhost:8080"
//Localhost for the computer is 127.0.0.1 or 192.168.245.46 for home network (from ipconfig IPv4 address)
//Localhost for Android emulator is 10.0.2.2
private val basketCourts = listOf(
    BasketCourt(name = "basketCourt0", imageUrl = "$BASE_URL/basketcourts/basketCourt0.jpg"),
    BasketCourt(name = "basketCourt1", imageUrl = "$BASE_URL/basketcourts/basketCourt1.jpg"),
    BasketCourt(name = "basketCourt2", imageUrl = "$BASE_URL/basketcourts/basketCourt2.jpg"),
    BasketCourt(name = "basketCourt3", imageUrl = "$BASE_URL/basketcourts/basketCourt3.jpg"),
    BasketCourt(name = "basketCourt4", imageUrl = "$BASE_URL/basketcourts/basketCourt4.jpg"),
    BasketCourt(name = "basketCourt5", imageUrl = "$BASE_URL/basketcourts/basketCourt5.jpg"),
)


fun Route.randomBasketCourt1() {
    //This is going to be the path after base Url to get the random basketCourt Object
    get("/randombasketcourt"){
        call.respond(
            HttpStatusCode.OK,
            basketCourts.random() //This is going to be the server reply, automatically parsed to a JSON string
        )
    }
}