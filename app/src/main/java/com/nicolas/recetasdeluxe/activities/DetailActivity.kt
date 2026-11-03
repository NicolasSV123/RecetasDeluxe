package com.nicolas.recetasdeluxe.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Pantalla de detalle para mostrar toda la informacion de una receta seleccionada
class DetailActivity: AppCompatActivity() {
    companion object{
        const val EXTRA_RECIPE = "recipe"
    }

    lateinit var binding: ActivityDetailBinding
    lateinit var recipe: Recipe
    private lateinit var recipeDAO: RecipeDAO
    private var favoriteMenuItem: MenuItem? = null
    private var isFavorite = false

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

        recipeDAO = RecipeDAO(this)

        // Obtenemos el ID de la receta enviado por el Intent y descargamos sus datos de la API
        val id = intent.getIntExtra(EXTRA_RECIPE, -1)
        val api = RecipesAPI.getInstance()
        CoroutineScope(Dispatchers.IO).launch{
            recipe = api.getRecipesByID(id)
            CoroutineScope(Dispatchers.Main).launch {
                loadData()
            }
        }
    }

    // Inflamos el menu superior que contiene el icono del corazon de favoritos
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_detail_menu, menu)
        favoriteMenuItem = menu.findItem(R.id.menu_favorite)
        updateFavoriteIcon()
        return true
    }

    // Gestionamos las acciones de los botones de la barra superior (Atras y Favorito)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // Cierra la pantalla y vuelve atras
                return true
            }
            R.id.menu_favorite -> {
                if (::recipe.isInitialized) {
                    // Si ya era favorita la borramos de SQLite, si no, la guardamos
                    if (isFavorite) {
                        recipeDAO.deleteFavorite(recipe.id)
                        isFavorite = false
                    } else {
                        recipeDAO.insertFavorite(recipe.id)
                        isFavorite = true
                    }
                    updateFavoriteIcon()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Actualiza la imagen del icono de favoritos en la barra superior segun si es favorita o no
    private fun updateFavoriteIcon() {
        favoriteMenuItem?.let { item ->
            val iconRes = if (isFavorite) R.drawable.ic_favorite_added else R.drawable.ic_favorite_empty
            item.setIcon(iconRes)
            item.icon?.setTint(ContextCompat.getColor(this, android.R.color.holo_blue_dark))
        }
    }

    // Rellenamos todos los componentes visuales con la informacion de la receta
    fun loadData(){
        supportActionBar?.title = recipe.name

        // Comprobamos en SQLite si esta receta ya es favorita
        isFavorite = recipeDAO.isFavorite(recipe.id)
        updateFavoriteIcon()

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

        // Boton para buscar videos de la receta en YouTube
        binding.youtubeButton.setOnClickListener {
            val query = "${recipe.name} recipes"
            val encodedQuery = Uri.encode(query)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")
            )
            startActivity(intent)
        }

        // Boton para buscar la receta en Google
        binding.googleButton.setOnClickListener {
            val query = "${recipe.name} recipes"
            val encodedQuery = Uri.encode(query)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encodedQuery"))
            startActivity(intent)
        }
    }
}
