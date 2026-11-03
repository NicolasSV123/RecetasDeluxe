package com.nicolas.recetasdeluxe.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.adapters.RecipeAdapter
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.ActivityFavoriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var recipeDAO: RecipeDAO
    private lateinit var adapter: RecipeAdapter
    private var favoriteRecipes: List<Recipe> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recipeDAO = RecipeDAO(this)

        // 1. Configuramos el RecyclerView reutilizando RecipeAdapter
        adapter = RecipeAdapter(favoriteRecipes) { recipe ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RECIPE, recipe.id)
            startActivity(intent)
        }

        binding.recyclerViewFavorites.adapter = adapter
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(this)

        // 2. Al pulsar el texto "¡Búscala!" redirige a inicio
        binding.emptySubtitleTextView.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // 3. Barra de navegación inferior
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

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    private fun loadFavorites() {
        val favoriteIds = recipeDAO.findAllFavoriteIds()

        if (favoriteIds.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewFavorites.visibility = View.GONE
            adapter.updateData(emptyList())
        } else {
            val api = RecipesAPI.getInstance()
            CoroutineScope(Dispatchers.IO).launch {
                val allRecipes = api.getRecipesList().recipes
                val filteredFavorites = allRecipes.filter { favoriteIds.contains(it.id) }

                CoroutineScope(Dispatchers.Main).launch {
                    if (filteredFavorites.isEmpty()) {
                        binding.emptyStateLayout.visibility = View.VISIBLE
                        binding.recyclerViewFavorites.visibility = View.GONE
                    } else {
                        binding.emptyStateLayout.visibility = View.GONE
                        binding.recyclerViewFavorites.visibility = View.VISIBLE
                        adapter.updateData(filteredFavorites)
                    }
                }
            }
        }
    }
}
