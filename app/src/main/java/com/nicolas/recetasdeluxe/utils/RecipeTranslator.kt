package com.nicolas.recetasdeluxe.utils

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.nicolas.recetasdeluxe.data.Recipe
import java.util.Locale

class RecipeTranslator {

    // Detectamos el idioma del dispositivo (ej: "es", "fr", etc.)
    private val deviceLanguageCode = Locale.getDefault().language

    // Configuramos el traductor de Ingles al idioma del dispositivo
    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(deviceLanguageCode.ifEmpty { TranslateLanguage.SPANISH })
        .build()

    private val translator: Translator = Translation.getClient(options)

    fun translateText(text: String, onTranslated: (String) -> Unit) {
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translated -> onTranslated(translated) }
                    .addOnFailureListener { onTranslated(text) }
            }
            .addOnFailureListener { e ->
                Log.e("TRANSLATOR", "Error al descargar modelo", e)
                onTranslated(text)
            }
    }

    fun translateList(texts: List<String>, onTranslated: (List<String>) -> Unit) {
        val conditions = DownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                val translatedList = mutableListOf<String>()
                var completed = 0

                if (texts.isEmpty()) {
                    onTranslated(emptyList())
                    return@addOnSuccessListener
                }

                texts.forEach { text ->
                    translator.translate(text)
                        .addOnSuccessListener { translated ->
                            translatedList.add(translated)
                            completed++
                            if (completed == texts.size) {
                                onTranslated(translatedList)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("TRANSLATOR", "Fallo al traducir: $text", e)
                            translatedList.add(text)
                            completed++
                            if (completed == texts.size) {
                                onTranslated(translatedList)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("TRANSLATOR", "Fallo al descargar modelo", e)
                onTranslated(texts)
            }
    }

    // Traduce una lista completa de objetos Recipe (nombre, cocina y dificultad) al idioma del dispositivo
    fun translateRecipes(recipes: List<Recipe>, onTranslated: (List<Recipe>) -> Unit) {
        val conditions = DownloadConditions.Builder().build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                val translatedRecipes = mutableListOf<Recipe>()
                var completed = 0

                if (recipes.isEmpty()) {
                    onTranslated(emptyList())
                    return@addOnSuccessListener
                }

                recipes.forEach { recipe ->
                    translateText(recipe.name) { transName ->
                        translateText(recipe.cuisine) { transCuisine ->
                            translateText(recipe.difficulty) { transDifficulty ->
                                val translatedRecipe = recipe.copy(
                                    name = transName,
                                    cuisine = transCuisine,
                                    difficulty = transDifficulty
                                )
                                synchronized(translatedRecipes) {
                                    translatedRecipes.add(translatedRecipe)
                                    completed++
                                    if (completed == recipes.size) {
                                        val ordered = recipes.map { orig ->
                                            translatedRecipes.find { it.id == orig.id } ?: orig
                                        }
                                        onTranslated(ordered)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener {
                onTranslated(recipes)
            }
    }

    fun close() {
        translator.close()
    }
}
