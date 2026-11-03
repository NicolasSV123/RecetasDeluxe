package com.nicolas.recetasdeluxe.activities

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.nicolas.recetasdeluxe.R
import com.nicolas.recetasdeluxe.adapters.MainPagerAdapter
import com.nicolas.recetasdeluxe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Configurar ViewPager2 y Adaptador
        val pagerAdapter = MainPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // 2. Al tocar icono en la barra -> cambiar ViewPager2
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.menu_favorite -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.menu_add_recipe -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                else -> false
            }
        }

        // 3. Al deslizar la pantalla (Swipe) -> actualizar icono seleccionado
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigationView.selectedItemId = when (position) {
                    0 -> R.id.menu_home
                    1 -> R.id.menu_favorite
                    2 -> R.id.menu_add_recipe
                    else -> R.id.menu_home
                }
            }
        })

        // 4. Controlar botón "Atrás" (Atrás regresa siempre a la pestaña 0 / Home)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewPager.currentItem != 0) {
                    binding.viewPager.currentItem = 0
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    fun navigateToHome() {
        binding.viewPager.currentItem = 0
    }
}
