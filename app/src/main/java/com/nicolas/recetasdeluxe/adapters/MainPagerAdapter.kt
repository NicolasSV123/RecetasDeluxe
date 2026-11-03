package com.nicolas.recetasdeluxe.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nicolas.recetasdeluxe.fragments.FavoriteFragment
import com.nicolas.recetasdeluxe.fragments.HomeFragment
import com.nicolas.recetasdeluxe.fragments.OriginalRecipesFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FavoriteFragment()
            2 -> OriginalRecipesFragment()
            else -> HomeFragment()
        }
    }
}
