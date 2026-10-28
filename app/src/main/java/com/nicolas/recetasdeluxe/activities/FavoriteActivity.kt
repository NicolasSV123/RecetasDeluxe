package com.nicolas.recetasdeluxe.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.databinding.ActivityFavoriteBinding // 1. Import

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding // 2. Variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 3. Inicialización
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNavigationView.selectedItemId = R.id.menu_favorite

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_favorite -> true
                R.id.menu_add_recipe -> {
                    startActivity(Intent(this, OriginalRecipesActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}