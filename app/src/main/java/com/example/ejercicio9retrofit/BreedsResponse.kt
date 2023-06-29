package com.example.ejercicio9retrofit

import com.google.gson.annotations.SerializedName

data class BreedsResponse(
    @SerializedName("message") val images: List<String>,
    val status: String
)
