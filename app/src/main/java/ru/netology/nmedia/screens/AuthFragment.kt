package ru.netology.nmedia.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.SignInViewModel

class AuthFragment : Fragment() {


    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAuthBinding.inflate(inflater,container,false)




        binding.buttonAuth.setOnClickListener {


            viewModel.signIn(binding.userLogin.text?.trim().toString(), binding.userPass.text?.trim().toString())

            findNavController().navigate(R.id.feedFragment)
            Toast.makeText(context,"Вы вошли в систему", Toast.LENGTH_SHORT).show()





        }







        return binding.root
    }

}