package com.nicolas.recetasdeluxe.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity: AppCompatActivity() {
    companion object{
        const val EXTRA_RECIPE = "recipe"
    }

    lateinit var binding: ActivityDetailBinding
    lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getIntExtra(EXTRA_RECIPE, -1)
        val api = RecipesAPI.getInstance()
        CoroutineScope(Dispatchers.IO).launch{
            recipe = api.getRecipesByID(id)
            CoroutineScope(Dispatchers.Main).launch {
                loadData()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun loadData(){
        supportActionBar?.title = recipe.name

        Picasso.get().load(recipe.image).into(binding.thumbnailImageView)



        binding.prepTimeMinutesChip.text = getString(R.string.preparation_minutes, recipe.prepTimeMinutes)
        binding.cookTimeMinutesChip.text = getString(R.string.cook_minutes, recipe.cookTimeMinutes)
        binding.caloriesChip.text = getString(R.string.recipe_calories, recipe.caloriesPerServing)
        binding.servingsChip.text = getString(R.string.recipe_servings, recipe.servings)

        binding.ingredientsTextView.text = recipe.ingredients.joinToString(separator = "\n") { ingredient ->
            "• $ingredient"
        }

        binding.instructionsTextView.text = recipe.instructions.mapIndexed { index, instruction ->
            "${index + 1}. $instruction"
        }.joinToString(separator = "\n\n")

        binding.youtubeButton.setOnClickListener {
            val query = "${recipe.name} recipes"
            val encodedQuery = android.net.Uri.encode(query)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")
            )
            startActivity(intent)
        }
        binding.googleButton.setOnClickListener {
            val query = "${recipe.name} recipes"
            val encodedQuery = android.net.Uri.encode(query)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encodedQuery"))
            startActivity(intent)
        }
    }
}