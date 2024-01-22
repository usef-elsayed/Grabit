package com.yousefelsayed.gamescheap.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.databinding.FragmentNoInternetBinding
import com.yousefelsayed.gamescheap.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoInternetFragment(): Fragment() {

    private lateinit var v: FragmentNoInternetBinding
    @Inject
    lateinit var networkManager: NetworkManager

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        v = DataBindingUtil.inflate(inflater, R.layout.fragment_no_internet,container,false)
        setUpListeners()
        return v.root
    }

    private fun setUpListeners(){
        v.internetScreenTryAgainButton.setOnClickListener {
            checkForInternetAndNavigate()
        }
    }
    private fun checkForInternetAndNavigate(){
        if (networkManager.checkForInternet()){
            findNavController().navigate(R.id.action_noInternetFragment_to_homeFragment)
        }else {
            showSnackBar("No internet found")
        }
    }
    private fun showSnackBar(msg: String){
        Snackbar.make(v.internetMainLayout,msg, Snackbar.LENGTH_SHORT).show()
    }
}