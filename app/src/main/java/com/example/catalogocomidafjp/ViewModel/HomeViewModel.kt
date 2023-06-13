package com.example.catalogocomidafjp.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.catalogocomidafjp.Room.Meal
import com.example.catalogocomidafjp.Room.MealList
import com.example.catalogocomidafjp.Retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var randomMealLiveData = MutableLiveData<Meal>()

    fun getRandomMeal() {
        RetrofitInstance.api.getRandomMeal().enqueue(object: Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if (response.body() != null) {
                    val randomMeal: Meal = response.body()!!.meals[0]
                    //Log.d("Test", "meal id ${randomMeal.idMeal} name ${randomMeal.strMeal}")
                    randomMealLiveData.value = randomMeal
                }
                else {
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("HomeFragmentRetrofit", t.message.toString())
            }
        })
    }

    fun observeRandomMealLiveData() : LiveData<Meal> {
        return randomMealLiveData
    }
}