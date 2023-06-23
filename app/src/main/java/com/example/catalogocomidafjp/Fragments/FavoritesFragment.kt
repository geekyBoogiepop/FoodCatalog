package com.example.catalogocomidafjp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.catalogocomidafjp.Adapters.FavoritesMealAdapter
import com.example.catalogocomidafjp.R
import com.example.catalogocomidafjp.Room.ClientModel
import com.example.catalogocomidafjp.Room.Meal
import com.example.catalogocomidafjp.Room.MealList
import com.example.catalogocomidafjp.ViewModel.MealViewModel
import com.example.catalogocomidafjp.databinding.FragmentFavoritesBinding
import com.example.catalogocomidafjp.databinding.FragmentHomeBinding
import java.util.concurrent.CompletableFuture


class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: MealViewModel
    private lateinit var favoritesMealAdapter: FavoritesMealAdapter
    var favorites: List<String> = emptyList()
    var meals: MutableList<Meal> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MealViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

//        var client = viewModel.getClientFromFireStore()
//        var favorites: List<String> = client?.favorites ?: emptyList()
//        var favorites: List<String> = emptyList()
//
//        viewModel.getClientFromFireStore { cliente ->
//            if (cliente != null) {
//                favorites = cliente?.favorites ?: emptyList()
//
//            }
//        }
//
//        Log.d("Favorites Fragment", "FFragment ${favorites}")
//        favoritesMealAdapter = FavoritesMealAdapter(favorites)
//
//        binding.rvFavorites.apply {
//            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
//            adapter = favoritesMealAdapter
//        }

        viewModel.getClientFromFireStore { cliente ->
            if (cliente != null) {
                favorites = cliente.favorites ?: emptyList()
                loadMeals()
            }
        }
    }

//    private fun loadMeals(){
//        for (mealId in favorites) {
//            viewModel.getMealInfo(mealId) { meal ->
//                if (meal != null) {
//                    meals.add(meal)
//                    Log.d("Favorite Fragment", "Meal Name: ${meal.strMeal}")
//                } else {
//                    Log.d("Favorite Fragment", "Error al recuperar detalles de la comida")
//                }
//            }
//        }
//        initializeFavoritesRecyclerView()
//    }

    private fun loadMeals() {
        val mealPromises = mutableListOf<CompletableFuture<Meal?>>()

        for (mealId in favorites) {
            val mealPromise = CompletableFuture<Meal?>()

            viewModel.getMealInfo(mealId) { meal ->
                mealPromise.complete(meal)
            }

            mealPromises.add(mealPromise)
        }

        CompletableFuture.allOf(*mealPromises.toTypedArray()).thenAccept {
            meals.clear()

            for (mealPromise in mealPromises) {
                val meal = mealPromise.get()
                if (meal != null) {
                    meals.add(meal)
                    Log.d("Favorite Fragment", "Meal Name: ${meal.strMeal}")
                } else {
                    Log.d("Favorite Fragment", "Error al recuperar detalles de la comida")
                }
            }

            initializeFavoritesRecyclerView()
        }
    }


    private fun initializeFavoritesRecyclerView() {
        Log.d("Favorites Fragment", "Favorites: $favorites")
        Log.d("Favorites Fragment", "Favorites Meals: $meals")
        favoritesMealAdapter = FavoritesMealAdapter(meals, viewModel)

        binding.rvFavorites.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = favoritesMealAdapter
        }
    }
}