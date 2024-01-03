package ru.netology.nmedia.screens


import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
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
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.viewmodel.AuthViewModel
import javax.inject.Inject


@AndroidEntryPoint
class AppActivity : AppCompatActivity() {
    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var messageService: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailability

    val viewModel by viewModels<AuthViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationsPermission()

        // эквивалент livedata, наблюдаем за обновлением меню
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.data.collect {
                    invalidateOptionsMenu()
                }
            }
        }


        messageService.token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("some stuff happened: ${task.exception}")
                return@addOnCompleteListener
            }

            val token = task.result
            println(token)
        }

        checkGoogleApiAvailability()

        requestNotificationsPermission()


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
                                appAuth.removeAuth()
                                Toast.makeText(
                                    applicationContext,
                                    "Вы вышли из системы",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .setNegativeButton("Остаться") { dialogInterface, i ->
                                return@setNegativeButton
                            }
                            .show()
                        true
                    }

                    else -> false
                }
            }
        })
    }

    fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(
                this@AppActivity,
                R.string.google_play_unavailable,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }
        requestPermissions(arrayOf(permission), 1)
    }
}

