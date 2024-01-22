package com.yousefelsayed.gamescheap.repository

import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.model.SearchArrayModel
import com.yousefelsayed.gamescheap.util.ApiUtilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchFragmentRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getSearchResults(searchQuery: String): Flow<SearchArrayModel> {
        return flow {
            emit(apiInterface.getSearchResults(ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_SEARCH + searchQuery))
        }.map {
            it.body()!!
        }
    }
}