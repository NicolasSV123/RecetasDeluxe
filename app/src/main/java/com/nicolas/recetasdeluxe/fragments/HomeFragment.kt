package com.nicolas.recetasdeluxe.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.activities.DetailActivity
import com.nicolas.recetasdeluxe.adapters.RecipeAdapter
import com.nicolas.recetasdeluxe.data.Recipe
import com.nicolas.recetasdeluxe.data.RecipesAPI
import com.nicolas.recetasdeluxe.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Fragment encargado de mostrar el catalogo principal de recetas de la API con buscador y filtros dinamicos
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecipeAdapter
    private var recipeList: List<Recipe> = emptyList()
    private var filteredRecipeList: List<Recipe> = emptyList()

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

        // Configuramos el menu superior (Lupa y Filtro) usando MenuProvider para este Fragment
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.activity_main_menu, menu)

                val searchMenuItem = menu.findItem(R.id.menu_search)
                val searchView = searchMenuItem?.actionView as? SearchView

                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        filteredRecipeList = recipeList.filter {
                            it.name.contains(newText, true)
                        }
                        showData()
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_filter -> {
                        showFilterDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Descargamos las recetas de la API
        val api = RecipesAPI.getInstance()
        CoroutineScope(Dispatchers.IO).launch {
            recipeList = api.getRecipesList().recipes
            filteredRecipeList = recipeList

            CoroutineScope(Dispatchers.Main).launch {
                adapter.updateData(recipeList)
                showData()
            }
        }
    }

    // Muestra el dialogo principal para elegir como filtrar de forma dinamica
    private fun showFilterDialog() {
        val options = arrayOf(
            getString(R.string.filter_all),
            getString(R.string.filter_by_difficulty),
            getString(R.string.filter_by_servings),
            getString(R.string.filter_by_cuisine),
            getString(R.string.filter_by_tag)
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.filter_title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        filteredRecipeList = recipeList
                        showData()
                        Toast.makeText(requireContext(), R.string.toast_showing_all, Toast.LENGTH_SHORT).show()
                    }
                    1 -> showDifficultyFilterDialog()
                    2 -> showServingsFilterDialog()
                    3 -> showCuisineFilterDialog()
                    4 -> showTagFilterDialog()
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    // Extrae y muestra dinamicamente todas las dificultades unicas de la API
    private fun showDifficultyFilterDialog() {
        val difficulties = recipeList.map { it.difficulty }.filter { it.isNotBlank() }.distinct().sorted().toTypedArray()

        if (difficulties.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_difficulties_available, Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_difficulty)
            .setItems(difficulties) { _, which ->
                val selectedDifficulty = difficulties[which]
                filteredRecipeList = recipeList.filter { it.difficulty.equals(selectedDifficulty, true) }
                showData()
                Toast.makeText(requireContext(), getString(R.string.toast_difficulty, selectedDifficulty), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    // Extrae y muestra dinamicamente todas las porciones unicas de la API
    private fun showServingsFilterDialog() {
        val servingsList = recipeList.map { it.servings }.distinct().sorted()

        if (servingsList.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_servings_available, Toast.LENGTH_SHORT).show()
            return
        }

        val options = servingsList.map { "$it personas" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_servings)
            .setItems(options) { _, which ->
                val selectedServings = servingsList[which]
                filteredRecipeList = recipeList.filter { it.servings == selectedServings }
                showData()
                Toast.makeText(requireContext(), getString(R.string.toast_servings, selectedServings), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    // Extrae y muestra dinamicamente todas las cocinas unicas de la API
    private fun showCuisineFilterDialog() {
        val cuisines = recipeList.map { it.cuisine }.filter { it.isNotBlank() }.distinct().sorted().toTypedArray()

        if (cuisines.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_cuisines_available, Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_cuisine)
            .setItems(cuisines) { _, which ->
                val selectedCuisine = cuisines[which]
                filteredRecipeList = recipeList.filter { it.cuisine.equals(selectedCuisine, true) }
                showData()
                Toast.makeText(requireContext(), getString(R.string.toast_cuisine, selectedCuisine), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    // Extrae y muestra dinamicamente todas las etiquetas unicas de la API
    private fun showTagFilterDialog() {
        val tags = recipeList.flatMap { it.tags }.filter { it.isNotBlank() }.distinct().sorted().toTypedArray()

        if (tags.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_tags_available, Toast.LENGTH_SHORT).show()
            return
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_tag)
            .setItems(tags) { _, which ->
                val selectedTag = tags[which]
                filteredRecipeList = recipeList.filter { it.tags.contains(selectedTag) }
                showData()
                Toast.makeText(requireContext(), getString(R.string.toast_tag, selectedTag), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showData() {
        adapter.updateData(filteredRecipeList)
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
