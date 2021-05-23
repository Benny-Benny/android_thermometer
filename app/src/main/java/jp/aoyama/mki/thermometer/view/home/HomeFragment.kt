package jp.aoyama.mki.thermometer.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import jp.aoyama.mki.thermometer.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var mBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(layoutInflater)
        mBinding.apply {
            val pageAdapter = PageAdapter(this@HomeFragment)
            viewPager.adapter = pageAdapter
            TabLayoutMediator(layoutTab, viewPager) { tab, position ->
                tab.text = pageAdapter.getPageTitle(position)
            }.attach()
        }
        return mBinding.root
    }
}