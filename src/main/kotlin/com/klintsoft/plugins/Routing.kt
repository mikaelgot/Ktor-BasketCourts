package com.klintsoft.plugins

import com.klintsoft.routes.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*

fun Application.configureRouting() {
    routing {
        uploadImage()
        uploadCourtWithImage()
        specificFile()
        uploadNewCourt()
        getAllBasketCourts()
        //randomBasketCourt1()
        get("/") {
            call.respondText("Hello Basket fans!")
        }
        //Path for static content
        //static was deprecated, this is the new way
        //remote path = the relative path in URL (in browser, etc.)
        //basePackage = the actual directory in the package
        staticResources("/basketcourts", "basketCourtImages")
    }
}
