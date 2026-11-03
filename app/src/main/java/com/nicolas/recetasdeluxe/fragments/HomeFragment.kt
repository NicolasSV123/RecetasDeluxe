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
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipeAdapter
    private var recipeList: List<Recipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RecipeAdapter(recipeList) { recipe ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RECIPE, recipe.id)
            startActivity(intent)
        }

        binding.recyclerViewHome.adapter = adapter
        binding.recyclerViewHome.layoutManager = LinearLayoutManager(requireContext())

        val api = RecipesAPI.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            recipeList = api.getRecipesList().recipes
            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(recipeList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
