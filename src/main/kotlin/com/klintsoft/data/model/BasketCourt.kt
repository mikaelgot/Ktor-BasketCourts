package com.klintsoft.data.model

import kotlinx.serialization.Serializable

@Serializable
data class BasketCourt(
    val id: Int? = null,
    val name: String = "Basket Court",
    val latitude: String = "60.1901035",
    val longitude: String =  "24.9170978",
    val description: String = "",
    val district: String = "",
    val numberOfBaskets: Int = 1,
    val isClosedCourt: Boolean = false,
    val terrain: String = "Asphalt",
    val isPaid: Boolean = false,
    val imageUrl: String = "",
)