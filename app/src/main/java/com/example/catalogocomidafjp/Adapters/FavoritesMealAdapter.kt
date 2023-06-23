package com.example.catalogocomidafjp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.catalogocomidafjp.R
import com.example.catalogocomidafjp.Room.ClientModel
import com.example.catalogocomidafjp.Room.Meal
import com.example.catalogocomidafjp.ViewModel.MealViewModel
import com.example.catalogocomidafjp.databinding.CategoryItemBinding

class FavoritesMealAdapter(
    var meals: MutableList<Meal>,
    var viewModel: MealViewModel
    )
    : RecyclerView.Adapter<FavoritesMealAdapter.FavoritesMealAdapterViewHolder>() {

    inner class FavoritesMealAdapterViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesMealAdapterViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritesMealAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritesMealAdapterViewHolder, position: Int) {
        val meal = meals.get(position)
        Glide.with(holder.itemView).load(meal.strMealThumb).into(holder.binding.imgMeal)
        holder.binding.tvMealName.text = meal.strMeal

        holder.binding.imgMeal.setOnClickListener {
            viewModel.saveFavoriteInFirestore(meal.idMeal, "LpZ7CSv8VlG8Toccf0HM") { code ->
                when (code) {
                    -1 -> {
                        Toast.makeText(holder.itemView.context, "Comida eliminada de favoritos", Toast.LENGTH_SHORT).show()
                        meals.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, meals.size)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return meals.size
    }
}