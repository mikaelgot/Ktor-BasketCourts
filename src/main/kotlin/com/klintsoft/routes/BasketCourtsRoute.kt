package com.klintsoft.routes

import com.google.gson.Gson
import com.google.gson.JsonDeserializer
import com.klintsoft.data.model.BasketCourt
import com.klintsoft.functions.readCourts
import com.klintsoft.functions.saveCourts
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import kotlin.io.use

//private const val BASE_URL = "http://localhost:8080"
private const val BASE_URL = "http://192.168.52.46:8080"
//Localhost for the computer is 127.0.0.1 or 192.168.xxx.xxx for home network (from ipconfig IPv4 address)
//Localhost for Android emulator is 10.0.2.2
private val basketCourts = mutableListOf(
    BasketCourt(id = 0, name = "basketCourt0", imageUrl = "$BASE_URL/basketcourts/0.jpg"),
    BasketCourt(id = 1, name = "basketCourt1", imageUrl = "$BASE_URL/basketcourts/1.jpg"),
    BasketCourt(id = 2, name = "basketCourt2", imageUrl = "$BASE_URL/basketcourts/2.jpg"),
    BasketCourt(id = 3, name = "basketCourt3", imageUrl = "$BASE_URL/basketcourts/3.jpg"),
    BasketCourt(id = 4, name = "basketCourt4", imageUrl = "$BASE_URL/basketcourts/4.jpg"),
    BasketCourt(id = 5, name = "basketCourt5", imageUrl = "$BASE_URL/basketcourts/5.jpg"),
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
            readCourts()
            //basketCourts //This is going to be the server reply, automatically parsed to a JSON string
        )
    }
}
fun Route.uploadNewCourt() {
    post("/newcourt") {
        val receivedCourt = call.receive<BasketCourt>()

        val isReplacing = basketCourts.removeIf { it.id == receivedCourt.id }

        if (isReplacing) { //Existing court data is being replaced
            basketCourts.add(receivedCourt)
        }
        else{ //New court (Shouldn't happen without image)
            val nextId = (basketCourts.maxBy { it.id ?: 0 }.id ?: 0 ) + 1
            val newCourt = receivedCourt.copy(id = nextId, imageUrl = "$BASE_URL/basketcourts/$nextId.jpg" )
            basketCourts.add(newCourt)
        }
        call.respond(HttpStatusCode.Created, "received court id: ${receivedCourt.id}")
    }
}
fun Route.specificFile() {
    route("/file") {
        get("{id?}") {
            val id = call.parameters["id"] //?: return@get call.respond(HttpStatusCode.BadRequest)
            id?.toIntOrNull()?.let { intId ->
                if(basketCourts.map { it.id }.contains(intId)) call.respond(HttpStatusCode.OK, basketCourts[intId])
                //else call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
        delete("{id?}"){
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (basketCourts.removeIf { (it.id?.toString() ?: "") == id }) {
                val file = File("build/resources/main/basketCourtImages/$id.jpg")
                file.delete()
                call.respondText("Court removed correctly", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not Found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
fun Route.uploadImage(){
    route("/image"){
        post {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> Unit
                    is PartData.FileItem -> {
                        //if(part.name == "image") {

                        val file = File("build/resources/main/static/basketCourts/${part.originalFileName}")

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

fun Route.uploadCourtWithImage() {
    route("/newcourtwithimage") {
        post {
            var courtData: String? = null
            var courtImagePart: PartData? = null
            var input: InputStream = InputStream.nullInputStream()

            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "court_data") {
                            courtData = part.value
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            courtImagePart = part
                            input = part.streamProvider()

                            /*part2.streamProvider().use { input ->
                                file.outputStream().buffered().use { output ->
                                    input.copyTo(output)
                                }
                            }*/
                        }
                    }
                    is PartData.BinaryItem -> Unit
                    else -> Unit
                }
            }
            if (courtData != null){//Court data has been received with or without image
                /** Creating new court or replacing existing one **/
                var imageId: Int?
                val receivedCourt = Json.decodeFromString<BasketCourt>(courtData!!)
                val isReplacing = basketCourts.removeIf { it.id == receivedCourt.id }
                if (isReplacing) { //Existing court is being replaced
                    imageId = receivedCourt.id
                    basketCourts.add(receivedCourt)
                }
                else{ //New court
                    val nextId = (basketCourts.maxBy { it.id ?: 0 }.id ?: 0 ) + 1
                    imageId = nextId
                    val newCourt = receivedCourt.copy(id = nextId, imageUrl = "$BASE_URL/basketcourts/$imageId.jpg")
                    basketCourts.add(newCourt)
                }
                saveCourts(basketCourts)

                if (courtImagePart != null) { //If image is not replaced this is null and the image stays the same
                    val file = File("build/resources/main/basketCourtImages/${imageId.toString()}.jpg")
                    //the save function was removed in Ktor 1.5.0,
                    // the recommended approach now is to use the streamProvider
                    file.outputStream().buffered().use { output -> input.copyTo(output) }
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}