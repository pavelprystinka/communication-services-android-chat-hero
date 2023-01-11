package com.example.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.chat.MainViewModel
import com.example.chat.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view: View = binding.root

        binding.run {

            nameTextEdit.addTextChangedListener {
                registerButton.isEnabled = nameTextEdit.text.toString().isNotEmpty()
            }

            registerButton.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.register(requireContext(), binding.nameTextEdit.text.toString())
                }
            }
        }

        return view
    }
}
