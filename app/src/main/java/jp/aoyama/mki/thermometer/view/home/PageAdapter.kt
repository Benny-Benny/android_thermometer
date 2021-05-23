package jp.aoyama.mki.thermometer.view.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import jp.aoyama.mki.thermometer.view.user.fragments.SelectNameFragment

class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SelectNameFragment.newInstance()
            else -> SelectNameFragment.newInstance()
        }
    }

    fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> "体温を記録"
            else -> ""
        }
    }
}