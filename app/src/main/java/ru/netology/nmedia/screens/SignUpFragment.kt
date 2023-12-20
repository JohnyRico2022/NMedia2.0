package ru.netology.nmedia.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.viewmodel.SignUpViewModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater, container, false)


        with(binding) {

            buttonSignUp.setOnClickListener {
                val userName = userName.text.trim().toString()
                val userLogin = userLogin.text.trim().toString()
                val userPass = userPass.text.trim().toString()
                val userPass2 = userPassRepeat.text.trim().toString()

                if (userName.isBlank() || userLogin.isBlank()
                    || userPass.isBlank() || userPass2.isBlank()
                ) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                } else if (userPass != userPass2) {
                    Toast.makeText(context, "Логины должны совпадать.", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.signUp(userName, userLogin, userPass2)
                    findNavController().navigate(R.id.feedFragment)
                    Toast.makeText(context, "Вы зарегистрировались", Toast.LENGTH_SHORT).show()
                }
            }

            binding.linkToAuth.setOnClickListener {
                findNavController().navigate(R.id.authFragment)
            }


        }




        return binding.root
    }
}