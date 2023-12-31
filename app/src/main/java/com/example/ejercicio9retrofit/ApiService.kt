package com.example.ejercicio9retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getImagesByBreeds(@Url url: String) : Response<BreedsResponse>

    @GET
    suspend fun getBreedsList(@Url url: String) : Response<Breeds>
}