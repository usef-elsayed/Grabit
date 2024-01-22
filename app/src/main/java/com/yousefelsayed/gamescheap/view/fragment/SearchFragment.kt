package com.yousefelsayed.gamescheap.view.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.adapter.SearchFragmentRecyclerAdapter
import com.yousefelsayed.gamescheap.api.Status
import com.yousefelsayed.gamescheap.databinding.FragmentSearchBinding
import com.yousefelsayed.gamescheap.model.SearchItemModel
import com.yousefelsayed.gamescheap.util.NetworkManager
import com.yousefelsayed.gamescheap.view.activity.WebActivity
import com.yousefelsayed.gamescheap.viewModel.SearchFragmentViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment: Fragment() {

    private lateinit var v: FragmentSearchBinding
    private lateinit var layoutManager: GridLayoutManager
    private val recyclerAdapter = SearchFragmentRecyclerAdapter()
    //Backend
    private val viewModel: SearchFragmentViewModel by viewModels()
    @Inject
    lateinit var networkManager: NetworkManager

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?):View {
        v = DataBindingUtil.inflate(inflater,R.layout.fragment_search,container,false)
        init()
        return v.root
    }

    private fun init(){
        //Recyclerview
        layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.orientation = RecyclerView.VERTICAL
        v.searchFragmentRecyclerView.layoutManager = layoutManager
        v.searchFragmentRecyclerView.setItemViewCacheSize(8)
        v.searchFragmentRecyclerView.setHasFixedSize(true)
        v.searchFragmentRecyclerView.adapter = recyclerAdapter
        viewModel.startSearch("\"\"")
        setupObservers()
        setupListeners()
    }
    private fun setupObservers(){
        lifecycleScope.launch {
            viewModel.searchData.collect{result ->
                when(result.status){
                    Status.SUCCESS -> {
                        if (result.data == null || result.data.size == 0){
                            hideShimmerLayout()
                            showErrorZeroResultsScreen()
                            hideErrorScreen()
                            return@collect
                        }
                        addRecyclerViewData(result.data)
                        hideShimmerLayout()
                        hideErrorZeroResultsScreen()
                        hideErrorScreen()
                    }
                    Status.ERROR -> {
                        showErrorScreen()
                        hideErrorZeroResultsScreen()
                        hideShimmerLayout()
                    }
                    Status.LOADING -> {
                        showShimmerLayout()
                    }
                }
            }
        }
    }
    private fun setupListeners(){
        recyclerAdapter.onItemClick = { clickedModel ->
            val intent = Intent(requireActivity(), WebActivity::class.java)
            intent.putExtra("dealId",clickedModel.cheapestDealID)
            startActivity(intent)
        }
        v.searchFragmentSearchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                checkForInternetAndNavigate()
                v.searchFragmentRecyclerView.scrollToPosition(0)
                searchFor(p0.toString())
                showShimmerLayout()
                return true
            }
        })
        //Error Screens
        v.searchFragmentErrorZeroResults.viewStub?.setOnInflateListener { _, inflated ->
            showViewWithAnimation(inflated.findViewById(R.id.zeroResultsLayout))
        }
        v.searchFragmentErrorScreen.viewStub?.setOnInflateListener { _, inflated ->
            inflated.findViewById<AppCompatButton>(R.id.zeroResultsScreenTryAgainButton).setOnClickListener {
                v.searchFragmentSearchView.setQuery("",false)
                viewModel.startSearch("\"\"")
                showSnackBar("Request Sent")
            }
        }
    }
    private fun checkForInternetAndNavigate(){
        if (!networkManager.checkForInternet()){
            findNavController().navigate(R.id.action_searchFragment_to_noInternetFragment)
        }
    }
    private fun addRecyclerViewData(apiCallResult: ArrayList<SearchItemModel>) {
        recyclerAdapter.submitList(apiCallResult)
    }
    private fun searchFor(query: String?){
        if (query.isNullOrEmpty() || query == "" || query == " "){
            viewModel.startSearch("\"\"")
            return
        }
        viewModel.startSearch(query)
    }
    private fun showShimmerLayout(){
        if (v.searchFragmentShimmerLayout.visibility == View.VISIBLE) return
        v.searchFragmentRecyclerView.visibility = View.GONE
        showViewWithAnimation(v.searchFragmentShimmerLayout)
        v.searchFragmentShimmerLayout.startShimmer()
    }
    private fun hideShimmerLayout(){
        if (v.searchFragmentShimmerLayout.visibility == View.GONE) return
        v.searchFragmentShimmerLayout.stopShimmer()
        v.searchFragmentShimmerLayout.visibility = View.GONE
        v.searchFragmentRecyclerView.visibility = View.VISIBLE
    }
    private fun showErrorZeroResultsScreen(){
        v.searchFragmentErrorZeroResults.viewStub?.visibility = View.VISIBLE

    }
    private fun hideErrorZeroResultsScreen(){
        if (v.searchFragmentErrorZeroResults.viewStub?.visibility == View.GONE) return
        v.searchFragmentErrorZeroResults.viewStub?.visibility = View.GONE
    }
    private fun showErrorScreen(){
        v.searchFragmentErrorScreen.viewStub?.visibility = View.VISIBLE
    }
    private fun hideErrorScreen(){
        if (v.searchFragmentErrorScreen.viewStub?.visibility == View.GONE) return
        v.searchFragmentErrorScreen.viewStub?.visibility = View.GONE
    }
    private fun showSnackBar(msg: String){
        Snackbar.make(v.searchFragmentMainLayout,msg, Snackbar.LENGTH_SHORT).show()
    }
    private fun showViewWithAnimation(v:View){
        if (v.visibility == View.VISIBLE) return
        v.alpha = 0.0f
        v.visibility = View.VISIBLE
        v.animate().alpha(1.0f).duration = 500
    }
}