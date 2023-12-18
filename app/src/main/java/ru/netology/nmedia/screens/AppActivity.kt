package ru.netology.nmedia.screens

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel


class AppActivity : AppCompatActivity() {


    val viewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // эквивалент livedata, наблюдаем за обновлением меню
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.data.collect {
                    invalidateOptionsMenu()
                }
            }
        }

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_auth, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                menu.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.singIn -> {
                        findNavController(R.id.container_fragments).navigate(R.id.authFragment)
                        true
                    }

                    R.id.singUp -> {
                        findNavController(R.id.container_fragments).navigate(R.id.signUpFragment)
                        true
                    }

                    R.id.singOut -> {
                        AlertDialog.Builder(this@AppActivity)
                            .setTitle("Уверены?")
                            .setPositiveButton("Выйти") { dialogInterface, i ->
                                AppAuth.getInstance().removeAuth()
                                Toast.makeText(applicationContext, "Вы вышли из системы", Toast.LENGTH_SHORT).show()
                            }
                            .setNegativeButton("Остаться"){ dialogInterface, i ->
                                return@setNegativeButton
                            }
 //                           .create()
                            .show()
                        true
                    }
                    else -> false
                }
            }
        })
    }
}

