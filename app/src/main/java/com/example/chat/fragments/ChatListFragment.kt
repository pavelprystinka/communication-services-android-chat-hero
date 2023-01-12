package com.example.chat.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.azure.android.communication.ui.chat.presentation.ChatThreadView
import com.example.chat.MainViewModel
import com.example.chat.databinding.FragmentChatListBinding
import com.example.chat.model.ChatThreadInfoViewModel
import com.example.chat.model.Tabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatListFragment : Fragment() {

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: FragmentChatListBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                this.isEnabled = false
                if (viewModel.selectedTab.value != Tabs.Chats || !closeChat())
                    requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)
        val view: View = binding.root

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.chatThreads.collect {
                val adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, it)
                binding.chatList.adapter = adapter
            }
        }

        binding.chatList.setOnItemClickListener { _, item, l, i ->
            viewModel.openedThread.value = viewModel.chatThreads.value[l]
        }

        CoroutineScope(Dispatchers.Main).launch{
            viewModel.openedThread.collect {
                it?.let { openChat(it) }
            }
        }
        return view
    }

    fun closeChat(): Boolean {
        viewModel.openedThread.value = null
        if (binding.chatContainer.isNotEmpty()) {
            binding.chatContainer.removeAllViews()
            return true
        }
        return false
    }

    private fun openChat(chatThreadInfoViewModel: ChatThreadInfoViewModel) {
        val context = this@ChatListFragment.requireContext()
        val chatAdapter = viewModel.getChatAdapter(this.requireContext(), chatThreadInfoViewModel.id)
        val chatView = ChatThreadView(context, chatAdapter)

        binding.chatContainer.addView(
            chatView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        onBackPressedCallback.isEnabled = true
    }
}
