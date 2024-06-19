package com.yousefelsayed.gamescheap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.api.Resource
import com.yousefelsayed.gamescheap.model.GameItemModel
import com.yousefelsayed.gamescheap.model.SessionFilterModel
import com.yousefelsayed.gamescheap.repository.GamesFragmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesFragmentViewModel @Inject constructor(private val gamesFragmentRepository: GamesFragmentRepository): ViewModel() {

    private val sessionFilterModel = SessionFilterModel()
    private val _gamesDeals = MutableStateFlow<Resource<ArrayList<GameItemModel>>>(Resource.loading())
    private var currentActiveJob: Job? = null
    val gamesDeals get() = _gamesDeals

    fun getStoresDeals(){
        currentActiveJob?.cancel()
        currentActiveJob = viewModelScope.launch(Dispatchers.IO) {
            delay(300)
            gamesFragmentRepository.getGamesDeals(sessionFilterModel.getStoresArrayInString(),sessionFilterModel.maxPrice,sessionFilterModel.pageNumber.toString(),sessionFilterModel.getSortMode()).catch { error ->
                _gamesDeals.value = if(error.message != null){
                    Resource.error(error.message!!)
                }else {
                    Resource.error("NULL")
                }
            }
                .collect{ result ->
                    gamesFragmentRepository.addToGamesList(result)
                    _gamesDeals.value = Resource.success(gamesFragmentRepository.getGamesList())
                }
        }
    }
    fun getMaximumPage(): String {
        return gamesFragmentRepository.getMaximumPage()
    }
    fun getCurrentGamesListSize(): Int {
        return gamesFragmentRepository.getCurrentGamesListSize()
    }
    private fun clearGamesList() {
        gamesFragmentRepository.clearGamesList()
    }
    fun changeMaxPrice(newMaxPrice: String){
        if (newMaxPrice.isNotEmpty() && newMaxPrice != "" && newMaxPrice != " "){
            sessionFilterModel.maxPrice = newMaxPrice
            resetPageNumber()
            clearGamesList()
        }
    }
    fun changeSortMode(newSortMode: String){
        sessionFilterModel.setSortMode(newSortMode)
        resetPageNumber()
        clearGamesList()
    }
    private fun resetPageNumber(){
        sessionFilterModel.pageNumber = 0
    }
    fun increasePageNumber(){
        sessionFilterModel.pageNumber++
    }
    fun getPageNumber(): Int{
        return sessionFilterModel.pageNumber
    }
}