package com.nicolas.recetasdeluxe.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicolas.recetasdeluxe.activities.DetailActivity
import com.nicolas.recetasdeluxe.adapters.RecipeAdapter
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.ActivityFavoriteBinding
import com.nicolas.recetasdeluxe.utils.RecipeTranslator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Fragment encargado de mostrar las recetas marcadas como favoritas traducidas
class FavoriteFragment : Fragment() {

    private var _binding: ActivityFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipeDAO: RecipeDAO
    private lateinit var adapter: RecipeAdapter
    private var favoriteRecipes: List<Recipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializamos el DAO de SQLite para consultar los IDs favoritos
        recipeDAO = RecipeDAO(requireContext())

        // Configuramos el adaptador para abrir el detalle al pulsar una receta favorita
        adapter = RecipeAdapter(favoriteRecipes) { recipe ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RECIPE, recipe.id)
            startActivity(intent)
        }

        binding.recyclerViewFavorites.adapter = adapter
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())


    }

    // Cada vez que volvemos a esta pestaña, recargamos los favoritos de SQLite
    override fun onResume() {
        super.onResume()
        loadFavorites()
    }

    // Funcion que lee los IDs de SQLite, filtra las recetas de la API y las traduce al idioma del telefono
    private fun loadFavorites() {
        val favoriteIds = recipeDAO.findAllFavoriteIds()

        // Si no hay favoritos guardados, mostramos el mensaje de aviso y ocultamos la lista
        if (favoriteIds.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewFavorites.visibility = View.GONE
            adapter.updateData(emptyList())
        } else {
            // Si hay favoritos, descargamos las recetas de la API y filtramos solo las marcadas
            val api = RecipesAPI.getInstance()
            CoroutineScope(Dispatchers.IO).launch {
                val allRecipes = api.getRecipesList().recipes
                val filteredFavorites = allRecipes.filter { favoriteIds.contains(it.id) }

                if (filteredFavorites.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.emptyStateLayout.visibility = View.VISIBLE
                        binding.recyclerViewFavorites.visibility = View.GONE
                    }
                } else {
                    // Traducimos las recetas favoritas al idioma del dispositivo antes de mostrarlas
                    val translator = RecipeTranslator()
                    translator.translateRecipes(filteredFavorites) { translatedFavorites ->
                        CoroutineScope(Dispatchers.Main).launch {
                            binding.emptyStateLayout.visibility = View.GONE
                            binding.recyclerViewFavorites.visibility = View.VISIBLE
                            adapter.updateData(translatedFavorites)
                            translator.close()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
