package com.nicolas.recetasdeluxe.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.adapters.OriginalRecipeAdapter
import com.nicolas.recetasdeluxe.data.MyRecipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.databinding.ActivityOriginalRecipesBinding
import com.nicolas.recetasdeluxe.databinding.DialogAddRecipeBinding

// Fragment encargado de mostrar, crear, editar y borrar tus propias recetas locales
class OriginalRecipesFragment : Fragment() {

    private var _binding: ActivityOriginalRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OriginalRecipeAdapter
    private lateinit var recipeDAO: RecipeDAO
    private var recipeList: List<MyRecipe> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityOriginalRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeDAO = RecipeDAO(requireContext())

        // Configuramos el adaptador con acciones para ver, editar o borrar cada receta propia
        adapter = OriginalRecipeAdapter(
            items = recipeList,
            onItemClick = { recipe -> showViewRecipeDialog(recipe) }, // Ver solo lectura
            onEditClick = { recipe -> showRecipeDialog(recipe) },      // Editar
            onDeleteClick = { recipe -> showDeleteConfirmationDialog(recipe) } // Borrar
        )

        binding.recyclerViewRecipesOriginal.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewRecipesOriginal.adapter = adapter

        // Boton flotante "+" para crear una nueva receta
        binding.addTaskButton.setOnClickListener {
            showRecipeDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        loadRecipes()
    }

    // Consulta las recetas propias en SQLite y actualiza el RecyclerView o el estado vacio
    private fun loadRecipes() {
        recipeList = recipeDAO.findAllMyRecipes()
        adapter.updateData(recipeList)

        if (recipeList.isEmpty()) {
            binding.emptyStateLayout.visibility = View.VISIBLE
            binding.recyclerViewRecipesOriginal.visibility = View.GONE
        } else {
            binding.emptyStateLayout.visibility = View.GONE
            binding.recyclerViewRecipesOriginal.visibility = View.VISIBLE
        }
    }

    // Muestra un dialogo en modo solo lectura al pulsar sobre una receta propia
    private fun showViewRecipeDialog(recipe: MyRecipe) {
        val dialogBinding = DialogAddRecipeBinding.inflate(layoutInflater)

        dialogBinding.nameEditText.setText(recipe.name)
        dialogBinding.ingredientsEditText.setText(recipe.ingredients)
        dialogBinding.instructionsEditText.setText(recipe.instructions)

        // Bloqueamos la edicion para que no se pueda modificar ni abrir el teclado
        dialogBinding.nameEditText.isFocusable = false
        dialogBinding.nameEditText.isFocusableInTouchMode = false
        dialogBinding.nameEditText.keyListener = null

        dialogBinding.ingredientsEditText.isFocusable = false
        dialogBinding.ingredientsEditText.isFocusableInTouchMode = false
        dialogBinding.ingredientsEditText.keyListener = null

        dialogBinding.instructionsEditText.isFocusable = false
        dialogBinding.instructionsEditText.isFocusableInTouchMode = false
        dialogBinding.instructionsEditText.keyListener = null

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(recipe.name)
            .setView(dialogBinding.root)
            .setCancelable(false) // Evita que se cierre al pulsar fuera
            .setPositiveButton(R.string.btn_close) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Muestra el dialogo para crear una receta nueva o editar una existente
    private fun showRecipeDialog(recipe: MyRecipe = MyRecipe(id = -1, name = "", ingredients = "", instructions = "")) {
        val dialogBinding = DialogAddRecipeBinding.inflate(layoutInflater)

        dialogBinding.nameEditText.setText(recipe.name)
        dialogBinding.ingredientsEditText.setText(recipe.ingredients)
        dialogBinding.instructionsEditText.setText(recipe.instructions)

        val dialogTitle = if (recipe.id == -1) getString(R.string.dialog_new_recipe) else getString(R.string.dialog_edit_recipe)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setCancelable(false) // Evita que se cierre al pulsar fuera
            .setPositiveButton(R.string.btn_save) { _, _ -> }
            .setNegativeButton(R.string.btn_cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = builder.create()
        dialog.show()

        // Validamos que el nombre no este vacio antes de guardar en SQLite
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val name = dialogBinding.nameEditText.text.toString().trim()
            val ingredients = dialogBinding.ingredientsEditText.text.toString().trim()
            val instructions = dialogBinding.instructionsEditText.text.toString().trim()

            if (name.isEmpty()) {
                dialogBinding.nameTextField.error = getString(R.string.error_empty_name)
            } else {
                recipe.name = name
                recipe.ingredients = ingredients
                recipe.instructions = instructions

                recipeDAO.save(recipe) // Inserta o actualiza en SQLite
                dialog.dismiss()
                loadRecipes()
            }
        }
    }

    // Muestra una alerta de confirmacion antes de borrar la receta de SQLite
    private fun showDeleteConfirmationDialog(recipe: MyRecipe) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, recipe.name))
            .setCancelable(false) // Evita que se cierre al pulsar fuera
            .setPositiveButton(R.string.btn_delete) { dialog, _ ->
                recipeDAO.delete(recipe)
                loadRecipes()
                Toast.makeText(requireContext(), R.string.toast_recipe_deleted, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.btn_cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
