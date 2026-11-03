package com.nicolas.recetasdeluxe.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nicolas.recetasdeluxe.fragments.FavoriteFragment
import com.nicolas.recetasdeluxe.fragments.HomeFragment
import com.nicolas.recetasdeluxe.fragments.OriginalRecipesFragment

// Este adaptador conecta el ViewPager2 de la pantalla principal con los tres Fragments
class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Le indica al ViewPager2 que tenemos 3 pestañas deslizables en total
    override fun getItemCount(): Int = 3

    // Segun la posicion activa (0, 1 o 2) devuelve el Fragment correspondiente
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment() // Pestaña de Inicio con recetas de la API
            1 -> FavoriteFragment() // Pestaña con recetas marcadas como favoritas
            2 -> OriginalRecipesFragment() // Pestaña para ver y crear recetas propias
            else -> HomeFragment()
        }
    }
}
