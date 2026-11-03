package com.nicolas.recetasdeluxe.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.adapters.OriginalRecipeAdapter
import com.nicolas.recetasdeluxe.data.MyRecipe
import com.nicolas.recetasdeluxe.data.RecipeDAO
import com.nicolas.recetasdeluxe.databinding.ActivityOriginalRecipesBinding
import com.nicolas.recetasdeluxe.databinding.DialogAddRecipeBinding

class OriginalRecipesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOriginalRecipesBinding
    private lateinit var adapter: OriginalRecipeAdapter
    private lateinit var recipeDAO: RecipeDAO
    private var recipeList: List<MyRecipe> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOriginalRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recipeDAO = RecipeDAO(this)

        adapter = OriginalRecipeAdapter(
            items = recipeList,
            onItemClick = { recipe ->
                // Modo solo lectura al pulsar el nombre / tarjeta
                showViewRecipeDialog(recipe)
            },
            onEditClick = { recipe ->
                // Modo edición al pulsar el botón del lápiz
                showRecipeDialog(recipe)
            },
            onDeleteClick = { recipe ->
                showDeleteConfirmationDialog(recipe)
            }
        )

        binding.recyclerViewRecipesOriginal.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecipesOriginal.adapter = adapter

        loadRecipes()

        binding.addTaskButton.setOnClickListener {
            showRecipeDialog()
        }

        // 👇 CÓDIGO DEL BOTTOM NAVIGATION 👇
        binding.bottomNavigationView.selectedItemId = R.id.menu_add_recipe

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_favorite -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    true
                }
                R.id.menu_add_recipe -> true
                else -> false
            }
        }
    }

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

    // Diálogo de lectura (solo lectura)
    private fun showViewRecipeDialog(recipe: MyRecipe) {
        val dialogBinding = DialogAddRecipeBinding.inflate(layoutInflater)

        dialogBinding.nameEditText.setText(recipe.name)
        dialogBinding.ingredientsEditText.setText(recipe.ingredients)
        dialogBinding.instructionsEditText.setText(recipe.instructions)

        // Bloquear edición manteniendo el texto oscuro
        dialogBinding.nameEditText.isFocusable = false
        dialogBinding.nameEditText.isFocusableInTouchMode = false
        dialogBinding.nameEditText.keyListener = null

        dialogBinding.ingredientsEditText.isFocusable = false
        dialogBinding.ingredientsEditText.isFocusableInTouchMode = false
        dialogBinding.ingredientsEditText.keyListener = null

        dialogBinding.instructionsEditText.isFocusable = false
        dialogBinding.instructionsEditText.isFocusableInTouchMode = false
        dialogBinding.instructionsEditText.keyListener = null

        MaterialAlertDialogBuilder(this)
            .setTitle(recipe.name)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_close) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Diálogo para crear o editar
    private fun showRecipeDialog(recipe: MyRecipe = MyRecipe(id = -1, name = "", ingredients = "", instructions = "")) {
        val dialogBinding = DialogAddRecipeBinding.inflate(layoutInflater)

        dialogBinding.nameEditText.setText(recipe.name)
        dialogBinding.ingredientsEditText.setText(recipe.ingredients)
        dialogBinding.instructionsEditText.setText(recipe.instructions)

        val dialogTitle = if (recipe.id == -1) getString(R.string.dialog_new_recipe) else getString(R.string.dialog_edit_recipe)

        val builder = MaterialAlertDialogBuilder(this)
            .setTitle(dialogTitle)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.btn_save) { _, _ -> }
            .setNegativeButton(R.string.btn_cancel) { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()

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

                recipeDAO.save(recipe)
                dialog.dismiss()
                loadRecipes()
            }
        }
    }

    private fun showDeleteConfirmationDialog(recipe: MyRecipe) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setMessage(getString(R.string.dialog_delete_message, recipe.name))
            .setPositiveButton(R.string.btn_delete) { dialog, _ ->
                recipeDAO.delete(recipe)
                loadRecipes()
                Toast.makeText(this, R.string.toast_recipe_deleted, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.btn_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
