package com.example.catalogocomidafjp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.catalogocomidafjp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigation = binding.bottomNavigation
        val navController = Navigation.findNavController(this, R.id.main_fragment)

        NavigationUI.setupWithNavController(bottomNavigation, navController)
    }
}