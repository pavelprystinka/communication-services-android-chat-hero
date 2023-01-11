package com.example.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.example.chat.fragments.ChatListFragment
import com.example.chat.fragments.ContactsFragment
import com.example.chat.fragments.LogInFragment
import com.example.chat.fragments.RegisterFragment
import com.example.chat.model.Tabs
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var logInFragment: LogInFragment
    private lateinit var registerFragment: RegisterFragment
    private lateinit var chatListFragment: ChatListFragment
    private lateinit var contactsFragment: ContactsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        logInFragment = LogInFragment()
        registerFragment = RegisterFragment()
        chatListFragment = ChatListFragment()
        contactsFragment = ContactsFragment()
        tabLayout.setupWithViewPager(viewPager)

        createTabs()

        if (savedInstanceState == null) {
            lifecycleScope.launch { viewModel.init() }
        }
    }

    private fun createTabs() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, 0)
        viewPagerAdapter.addFragment(logInFragment, "LogIn")
        viewPagerAdapter.addFragment(registerFragment, "Register")
        viewPagerAdapter.addFragment(chatListFragment, "Chats")
        viewPagerAdapter.addFragment(contactsFragment, "Contacts")
        viewPager.adapter = viewPagerAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.selectedTab.value = Tabs.valueOf(tab.text.toString())
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        lifecycleScope.launch {
            viewModel.displayLogIn.collect {
                tabLayout.getTabAt(0)?.view?.visibility = if (it) View.VISIBLE else View.GONE

                if (!it)
                    tabLayout.selectTab(tabLayout.getTabAt(2))
            }
        }

        lifecycleScope.launch {
            viewModel.displayRegister.collect {
                tabLayout.getTabAt(1)?.view?.visibility = if (it) View.VISIBLE else View.GONE

                if (!it)
                    tabLayout.selectTab(tabLayout.getTabAt(2))
            }
        }

        lifecycleScope.launch {
            viewModel.displayChats.collect {
                tabLayout.getTabAt(2)?.view?.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.displayContacts.collect {
                tabLayout.getTabAt(3)?.view?.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        lifecycleScope.launch {
            viewModel.selectedTab.collect {
                tabLayout.selectTab(tabLayout.getTabAt(it.ordinal))
            }
        }
    }

    private class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        private val fragments: MutableList<Fragment> = ArrayList()
        private val fragmentTitles: MutableList<String> = ArrayList()

        fun addFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            fragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        @Nullable
        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitles[position]
        }
    }
}