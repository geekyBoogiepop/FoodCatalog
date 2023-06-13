package com.example.catalogocomidafjp.Retrofit

import com.example.catalogocomidafjp.Model.MealList
import retrofit2.Call
import retrofit2.http.GET

interface MealAPI {

    @GET("random.php")
    fun getRandomMeal() : Call<MealList>
}