package com.example.a74.fragments.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.a74.fragments.GroupsFragment
import com.example.a74.fragments.PeopleFragment

class ViewPagerAdapter(
    var fragmentManager: FragmentManager,
    var list: List<Fragment>,
    var lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return list.size
    }


    override fun createFragment(position: Int): Fragment {
        var fr = Fragment()
        when (position) {
            0 -> fr = PeopleFragment()
            1 -> fr = GroupsFragment()
        }
        return fr
    }

}