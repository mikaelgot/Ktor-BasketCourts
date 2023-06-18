package com.klintsoft.routes

import com.klintsoft.data.model.BasketCourt
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

private const val BASE_URL = "http://localhost:8080"
//Localhost for the computer is 127.0.0.1 or 192.168.245.46 for home network (from ipconfig IPv4 address)
//Localhost for Android emulator is 10.0.2.2
private val basketCourts = mutableListOf(
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
fun Route.getAllBasketCourts() {
    //This is going to be the path after base Url to get the random basketCourt Object
    get("/allbasketcourts"){
        call.respond(
            HttpStatusCode.OK,
            basketCourts //This is going to be the server reply, automatically parsed to a JSON string
        )
    }
}
fun Route.uploadFile() {
    route("/file") {
        get {
            call.respond(HttpStatusCode.OK, basketCourts[0])
        }
        post {
            val basketCourt = call.receive<BasketCourt>()
            basketCourts.add(basketCourt)
            call.respond(HttpStatusCode.Created)
        }
        post {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> Unit
                    is PartData.FileItem -> {
                        //if(part.name == "image") {
                        val file = File("main/${part.originalFileName}")
                        //the save function was removed in Ktor 1.5.0,
                        // the recommended approach now is to use the streamProvider
                        part.streamProvider().use { input ->
                            file.outputStream().buffered().use { output ->
                                input.copyTo(output)
                            }
                        }
                        //}
                    }

                    else -> Unit
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}