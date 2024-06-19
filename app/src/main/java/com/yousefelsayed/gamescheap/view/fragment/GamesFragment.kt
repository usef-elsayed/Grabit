package com.yousefelsayed.gamescheap.view.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.adapter.GamesFragmentRecyclerAdapter
import com.yousefelsayed.gamescheap.api.Status
import com.yousefelsayed.gamescheap.databinding.FragmentGamesBinding
import com.yousefelsayed.gamescheap.model.GameItemModel
import com.yousefelsayed.gamescheap.util.NetworkManager
import com.yousefelsayed.gamescheap.view.activity.MainActivity
import com.yousefelsayed.gamescheap.view.activity.WebActivity
import com.yousefelsayed.gamescheap.viewModel.GamesFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class GamesFragment: Fragment() {

    private lateinit var v: FragmentGamesBinding
    //Design
    private val sortSpinnerOptions by lazy{ listOf("Deal Rating","Title","Savings","Price","Metacritic","Reviews","Release","Store","Recent") }
    private var getRequestIsSent = false
    private var canLoadMoreItems = true
    private val recyclerAdapter: GamesFragmentRecyclerAdapter = GamesFragmentRecyclerAdapter()
    private lateinit var layoutManager: GridLayoutManager
    //Backend
    private val viewModel: GamesFragmentViewModel by viewModels()
    @Inject
    lateinit var networkManager: NetworkManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        v = DataBindingUtil.inflate(inflater, R.layout.fragment_games, container, false)
        init()
        return v.root
    }

    private fun init(){
        //Setup Recyclerview
        layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.orientation = RecyclerView.VERTICAL
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(recyclerAdapter.getItemViewType(position)){
                    0 -> {
                        1
                    }
                    1 -> {
                        2
                    }
                    else -> {
                        2
                    }
                }
            }

        }
        v.gamesFragmentRecyclerView.layoutManager = layoutManager
        v.gamesFragmentRecyclerView.setItemViewCacheSize(8)
        v.gamesFragmentRecyclerView.setHasFixedSize(true)
        v.gamesFragmentRecyclerView.adapter = recyclerAdapter
        //Setup spinner
        val spinnerAdapter = ArrayAdapter(requireContext(),R.layout.item_spinner_games_fragment, sortSpinnerOptions)
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_item_games_fragment)
        v.gamesFragmentSortBySpinner.adapter = spinnerAdapter
        setupObservers()
        setupListeners()
    }
    private fun checkForInternetAndNavigate(){
        if (!networkManager.checkForInternet()){
            findNavController().navigate(R.id.action_gamesFragment_to_noInternetFragment)
        }
    }
    private fun setupObservers(){
        lifecycleScope.launch {
            viewModel.gamesDeals.collect{result ->
                when(result.status){
                    Status.SUCCESS -> {
                        getRequestIsSent = false
                        if (result.data!!.size == 0 && viewModel.getCurrentGamesListSize() == 0){
                            showErrorZeroResultsScreen()
                        }else {
                            hideErrorZeroResultsScreen()
                            hideErrorScreen()
                            addRecyclerViewData(result.data)
                            hideShimmerLayout()
                        }
                    }
                    Status.ERROR -> {
                        hideShimmerLayout()
                        showErrorScreen()
                        getRequestIsSent = false
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
            val intent = Intent(this@GamesFragment.context, WebActivity::class.java)
            intent.putExtra("dealId",clickedModel.dealID)
            startActivity(intent)
        }
        v.gamesFragmentRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!canLoadMoreItems) return
                if (v.gamesFragmentRecyclerView.canScrollVertically(1)) return
                startApiRequest(shouldIncreasePageNumber = true)
                v.gamesFragmentRecyclerView.scrollToPosition(viewModel.getCurrentGamesListSize() - 1)
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        //Filter options
        v.gamesFragmentSortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.changeSortMode(sortSpinnerOptions[position])
                v.gamesFragmentRecyclerView.scrollToPosition(0)
                startApiRequest()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        v.gamesFragmentMaxPriceEditText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                viewModel.changeMaxPrice(s.toString())
                v.gamesFragmentRecyclerView.scrollToPosition(0)
                startApiRequest(shouldIncreasePageNumber = false, shouldOverrideOneRequest = true)
                showShimmerLayout()
            }
        })
        //Error Screens
        v.gamesFragmentErrorZeroResults.viewStub?.setOnInflateListener { _, inflated ->
            showViewWithAnimation(inflated.findViewById(R.id.zeroResultsLayout))
        }
        v.gamesFragmentErrorScreen.viewStub?.setOnInflateListener { _, inflated ->
            inflated.findViewById<AppCompatButton>(R.id.zeroResultsScreenTryAgainButton).setOnClickListener {
                startApiRequest()
                showSnackBar("Request Sent")
            }
        }
    }
    private fun startApiRequest(shouldIncreasePageNumber: Boolean = false,shouldOverrideOneRequest: Boolean = false) {
        if (getRequestIsSent && !shouldOverrideOneRequest) return
        checkForInternetAndNavigate()
        if (viewModel.getPageNumber() > viewModel.getMaximumPage().toInt()) {
            canLoadMoreItems = false
            return
        }
        if (shouldIncreasePageNumber) viewModel.increasePageNumber()
        viewModel.getStoresDeals()
        getRequestIsSent = true
    }
    private fun addRecyclerViewData(apiCallResult: ArrayList<GameItemModel>) {
        recyclerAdapter.submitList(apiCallResult)
    }
    private fun showShimmerLayout(){
        v.gamesFragmentRecyclerView.visibility = View.GONE
        showViewWithAnimation(v.gamesFragmentShimmerLayout)
        v.gamesFragmentShimmerLayout.startShimmer()
    }
    private fun hideShimmerLayout(){
        v.gamesFragmentShimmerLayout.stopShimmer()
        v.gamesFragmentShimmerLayout.visibility = View.GONE
        v.gamesFragmentRecyclerView.visibility = View.VISIBLE
    }
    private fun showErrorZeroResultsScreen(){
        v.gamesFragmentErrorZeroResults.viewStub?.visibility = View.VISIBLE
    }
    private fun hideErrorZeroResultsScreen(){
        if (v.gamesFragmentErrorZeroResults.viewStub?.visibility == View.GONE) return
        v.gamesFragmentErrorZeroResults.viewStub?.visibility = View.GONE
    }
    private fun showErrorScreen(){
        v.gamesFragmentErrorScreen.viewStub?.visibility = View.VISIBLE
    }
    private fun hideErrorScreen(){
        if (v.gamesFragmentErrorScreen.viewStub?.visibility == View.GONE) return
        v.gamesFragmentErrorScreen.viewStub?.visibility = View.GONE
    }
    private fun showViewWithAnimation(v:View){
        if (v.visibility == View.VISIBLE) return
        v.alpha = 0.0f
        v.visibility = View.VISIBLE
        v.animate().alpha(1.0f).duration = 500
    }
    private fun showSnackBar(msg: String){
        Snackbar.make(v.gamesFragmentMainLayout,msg,Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        v.gamesFragmentRecyclerView.adapter = null
        super.onDestroyView()
    }
}