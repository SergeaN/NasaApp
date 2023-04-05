package ru.sergean.nasaapp.presentation.ui.intro

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class IntroAdapter(
    private val introItemData: List<IntroItemData>, fragment: IntroFragment,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = introItemData.size

    override fun createFragment(position: Int): Fragment =
        IntroItemFragment.newInstance(data = introItemData[position])

}