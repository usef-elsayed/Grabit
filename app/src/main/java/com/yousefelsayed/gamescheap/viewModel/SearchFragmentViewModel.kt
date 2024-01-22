package com.yousefelsayed.gamescheap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.api.Resource
import com.yousefelsayed.gamescheap.model.SearchArrayModel
import com.yousefelsayed.gamescheap.repository.SearchFragmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(val repository: SearchFragmentRepository): ViewModel() {

    private val _searchData : MutableStateFlow<Resource<SearchArrayModel>> = MutableStateFlow(Resource.loading())
    val searchData : MutableStateFlow<Resource<SearchArrayModel>> get() = _searchData

    fun startSearch(searchQuery: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSearchResults(searchQuery).catch { error ->
                if(error.message != null){
                    _searchData.value = Resource.error(error.message!!)
                }else {
                    _searchData.value = Resource.error("Unspecified error")
                }
            }.collect{ result ->
                _searchData.value = Resource.success(result)
            }
        }
    }
}