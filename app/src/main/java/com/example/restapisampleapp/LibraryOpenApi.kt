package com.example.restapisampleapp

import com.example.restapisampleapp.data.Library
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

class LibraryOpenApi {
    companion object {
        val DOMAIN = "http://openapi.seoul.go.kr:8088/"
        val API_KEY = "## here!! add user api key ##"
    }
}

interface LibraryOpenService {
    @GET("{api_key}/json/SeoulPublicLibraryInfo/1/200")
    fun  getLibrary(@Path("api_key") key: String): Call<Library>
}