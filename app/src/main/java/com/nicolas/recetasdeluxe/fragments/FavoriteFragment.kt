package com.nicolas.recetasdeluxe.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nicolas.recetasdeluxe.activities.DetailActivity
import com.nicolas.recetasdeluxe.activities.MainActivity
import com.nicolas.recetasdeluxe.adapters.RecipeAdapter
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.ActivityFavoriteBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        recipeDAO = RecipeDAO(requireContext())

        adapter = RecipeAdapter(favoriteRecipes) { recipe ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RECIPE, recipe.id)
            startActivity(intent)
        }

        binding.recyclerViewFavorites.adapter = adapter
        binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())

        binding.emptySubtitleTextView.setOnClickListener {
            val mainActivity = activity as? MainActivity
            mainActivity?.navigateToHome()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
