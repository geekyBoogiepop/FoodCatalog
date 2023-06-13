package com.example.catalogocomidafjp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.catalogocomidafjp.Activities.MealActivity
import com.example.catalogocomidafjp.Room.Meal
import com.example.catalogocomidafjp.ViewModel.HomeViewModel
import com.example.catalogocomidafjp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var randomMeal: Meal

    companion object {
        const val MEAL_ID = "com.example.catalogocomidafjp.Fragments.idMeal"
        const val MEAL_NAME = "com.example.catalogocomidafjp.Fragments.nameMeal"
        const val MEAL_THUMB = "com.example.catalogocomidafjp.Fragments.thumbMeal"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getRandomMeal()
        observerRandomMeal()
        onRandomMealClick()
    }

    private fun onRandomMealClick() {
        binding.randomMealImage.setOnClickListener {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, randomMeal.idMeal)
            intent.putExtra(MEAL_NAME, randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)

            startActivity(intent)
        }
    }

    private fun observerRandomMeal() {
        viewModel.observeRandomMealLiveData().observe(viewLifecycleOwner, { meal ->
            Glide.with(this@HomeFragment)
                .load(meal!!.strMealThumb)
                .into(binding.randomMealImage)

            this.randomMeal = meal
        })
    }
}