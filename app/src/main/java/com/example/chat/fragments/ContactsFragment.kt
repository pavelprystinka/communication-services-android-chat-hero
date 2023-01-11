package com.example.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.chat.MainViewModel
import com.example.chat.databinding.FragmentContactsBinding
import com.example.chat.model.Tabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {

    private lateinit var binding: FragmentContactsBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentContactsBinding.inflate(inflater, container, false)
        val view: View = binding.root

        CoroutineScope(Dispatchers.Main).launch {
            val users = viewModel.getUsers().filter { user -> user.id != viewModel.currentUser?.id }

            val listView = binding.list

            val adapter = ArrayAdapter(
                view.context,
                android.R.layout.simple_list_item_multiple_choice,
                users
            )

            listView.adapter = adapter

            listView.setOnItemClickListener { _, item, l, _ ->
                if ((item as AppCompatCheckedTextView).isChecked) {
                    viewModel.addSelectedContact(users[l])
                } else {
                    viewModel.removeSelectedContact(users[l])
                }
            }
        }

        binding.startChatButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createChatThread()
                viewModel.selectedTab.value = Tabs.Chats
            }
        }

        lifecycleScope.launch {
            viewModel.canStartThread.collect { binding.startChatButton.isEnabled = it }
        }

        return view
    }
}
