package com.klintsoft.plugins

import com.klintsoft.routes.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*

fun Application.configureRouting() {
    routing {
        randomBasketCourt1()
        get("/") {
            call.respondText("Hello Basket fans!")
        }
        static("/static"){
            resources("static")
        }
    }
}
