package com.example.catalogocomidafjp.Activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.catalogocomidafjp.Fragments.HomeFragment
import com.example.catalogocomidafjp.R
import com.example.catalogocomidafjp.Room.Meal
import com.example.catalogocomidafjp.ViewModel.MealViewModel
import com.example.catalogocomidafjp.databinding.ActivityMealBinding

class MealActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMealBinding
    private lateinit var mealId: String
    private lateinit var mealName: String
    private lateinit var mealThumb: String
    private lateinit var youtubeLink: String
    private lateinit var viewModel: MealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MealViewModel::class.java]

        getMealInformationFromIntent()
        setInformationInViews()

        loadingCase()
        viewModel.getMealDetail(mealId)
        observerMealDetailsLiveData()

        onYoutubeImageClick()
    }

    private fun onYoutubeImageClick() {
        binding.imageYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }

    private fun observerMealDetailsLiveData() {
        viewModel.observerMealDetailsLiveData().observe(this, object: Observer<Meal> {
            override fun onChanged(t: Meal?) {
                onResponseCase()
                val meal = t

                binding.tvCategoria.text = "Categoria: ${meal!!.strCategory}"
                binding.tvOrigen.text = "Origen: ${meal!!.strArea}"
                binding.tvReceta.text = meal!!.strInstructions
                youtubeLink = meal.strYoutube
            }
        })
    }

    private fun setInformationInViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imageMealDetail)

        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))

    }

    private fun getMealInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
    }

    private fun loadingCase() {
        binding.progressbar.visibility = View.VISIBLE
        binding.btnAddToFavorites.visibility = View.INVISIBLE
        binding.tvOrigen.visibility = View.INVISIBLE
        binding.tvReceta.visibility = View.INVISIBLE
        binding.tvCategoria.visibility = View.INVISIBLE
        binding.imageYoutube.visibility = View.INVISIBLE
    }

    private fun onResponseCase() {
        binding.progressbar.visibility = View.INVISIBLE
        binding.btnAddToFavorites.visibility = View.VISIBLE
        binding.tvOrigen.visibility = View.VISIBLE
        binding.tvReceta.visibility = View.VISIBLE
        binding.tvCategoria.visibility = View.VISIBLE
        binding.imageYoutube.visibility = View.VISIBLE
    }
}