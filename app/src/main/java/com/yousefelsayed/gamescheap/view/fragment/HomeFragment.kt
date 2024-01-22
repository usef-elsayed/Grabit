package com.yousefelsayed.gamescheap.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.adapter.HomeScreenGamesViewPagerAdapter
import com.yousefelsayed.gamescheap.api.Status
import com.yousefelsayed.gamescheap.databinding.FragmentHomeBinding
import com.yousefelsayed.gamescheap.model.GamesArrayModel
import com.yousefelsayed.gamescheap.util.HorizontalMarginItemDecoration
import com.yousefelsayed.gamescheap.util.NetworkManager
import com.yousefelsayed.gamescheap.view.activity.WebActivity
import com.yousefelsayed.gamescheap.viewModel.HomeFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private lateinit var v: FragmentHomeBinding
    //Backend
    private val viewModel: HomeFragmentViewModel by viewModels()
    @Inject
    lateinit var networkManager: NetworkManager

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        v = DataBindingUtil.inflate(inflater, R.layout.fragment_home,container,false)
        init()
        return v.root
    }

    private fun init(){
        setupListener()
        setupObservers()
        checkForInternetAndNavigate()
    }
    private fun setupObservers(){
        lifecycleScope.launch {
            viewModel.steamEpicGames.collect{result ->
                when(result.status){
                    Status.SUCCESS -> {
                        hideShimmer()
                        hideErrorScreen()
                        setupViewpager(result.data!!)
                    }
                    Status.ERROR -> {
                        showErrorScreen()
                    }
                    Status.LOADING -> {
                        showSimmer()
                    }
                }
            }
        }
    }
    private fun setupListener(){
        v.homeFragmentErrorScreen.viewStub?.setOnInflateListener { _, inflated ->
            inflated.findViewById<AppCompatButton>(R.id.zeroResultsScreenTryAgainButton).setOnClickListener {
                viewModel.getSteamAndEpicGames()
                showSnackBar("Request Sent")
            }
        }
    }
    private fun setupViewpager(apiCallResult: GamesArrayModel){
        val adapter = HomeScreenGamesViewPagerAdapter(apiCallResult)
        v.homeFragmentViewPager.adapter = adapter
        v.homeFragmentViewPager.offscreenPageLimit = 1
        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            //Height effect
            page.scaleY = 1 - (0.25f * abs(position))
            //Fading effect
             page.alpha = 0.5f + (1 - abs(position))
        }
        v.homeFragmentViewPager.setPageTransformer(pageTransformer)
        val itemDecoration = HorizontalMarginItemDecoration(requireContext(), R.dimen.viewpager_current_item_horizontal_margin)
        v.homeFragmentViewPager.addItemDecoration(itemDecoration)
        adapter.onItemClick = { clickedModel ->
            val intent = Intent(this@HomeFragment.context,WebActivity::class.java)
            intent.putExtra("dealId",clickedModel.dealID)
            startActivity(intent)
        }
    }
    private fun checkForInternetAndNavigate(){
        if (!networkManager.checkForInternet()){
            findNavController().navigate(R.id.action_homeFragment_to_noInternetFragment)
        }else {
            viewModel.getSteamAndEpicGames()
        }
    }
    private fun showErrorScreen(){
        v.homeFragmentErrorScreen.viewStub?.visibility = View.VISIBLE
    }
    private fun hideErrorScreen(){
        if (v.homeFragmentErrorScreen.viewStub?.visibility == View.GONE) return
        v.homeFragmentErrorScreen.viewStub?.visibility = View.GONE
    }
    private fun showSnackBar(msg: String){
        Snackbar.make(v.homeFragmentMainLayout,msg, Snackbar.LENGTH_SHORT).show()
    }
    private fun showSimmer(){
        v.homeFragmentViewPager.visibility = View.INVISIBLE
        v.homeFragmentShimmer.visibility = View.VISIBLE
        v.homeFragmentShimmer.startShimmer()
    }
    private fun hideShimmer(){
        v.homeFragmentShimmer.stopShimmer()
        v.homeFragmentShimmer.visibility = View.GONE
        v.homeFragmentViewPager.visibility = View.VISIBLE
    }
}