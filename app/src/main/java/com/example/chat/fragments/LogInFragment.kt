package com.example.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.chat.MainViewModel
import com.example.chat.databinding.FragmentLogInBinding
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLogInBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLogInBinding.inflate(inflater, container, false)
        val view: View = binding.root

        lifecycleScope.launch {
            val users = viewModel.getUsers()

            val listView = binding.list
            listView.choiceMode = ListView.CHOICE_MODE_SINGLE

            val adapter = ArrayAdapter(
                view.context,
                    android.R.layout.simple_spinner_dropdown_item,
                users
            )

            listView.adapter = adapter

            listView.setOnItemClickListener { _, item, l, i ->
                item.isSelected = true
                viewModel.currentUser = users[l]
                binding.loginButton.isEnabled = true
            }
        }

        binding.loginButton.setOnClickListener {
            lifecycleScope.launch { viewModel.logIn(this@LogInFragment.requireContext()) }
        }

        return view
    }
}
