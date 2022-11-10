package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.databinding.FragmentAuthorizationBinding
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.viewmodel.AuthorizationViewModel


class AuthorizationFragment : Fragment() {

    private var _binding: FragmentAuthorizationBinding? = null
    private val binding get() = _binding!!

    val viewModel: AuthorizationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthorizationBinding.inflate(layoutInflater, container, false)

        binding.buttonEnter.setOnClickListener {
            viewModel.updateUser(
                binding.login.text.toString(),
                binding.password.text.toString()
            )
        }
        viewModel.data.observe(viewLifecycleOwner, {
            AppAuth.getInstance().setAuth(it.id, it.token!!)
            findNavController().navigateUp()
        })

        // установка тоста по ошибке авторизации
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Toast.makeText(activity, "Authorization error", Toast.LENGTH_SHORT).show()
            }
        }



        return binding.root
    }
}