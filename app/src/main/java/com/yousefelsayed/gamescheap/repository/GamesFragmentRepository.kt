package com.yousefelsayed.gamescheap.repository

import android.util.Log
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.model.GameItemModel
import com.yousefelsayed.gamescheap.model.GamesArrayModel
import com.yousefelsayed.gamescheap.util.ApiUtilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GamesFragmentRepository @Inject constructor(private val apiInterface: ApiInterface) {
    private var gamesList =  ArrayList<GameItemModel>()
    private var pagesMax :String = "50"

    //Page Max
    suspend fun getGamesDeals(storesID:String, maxPrice:String, pageNumber:String, sortMode:String): Flow<GamesArrayModel> {
        if (storesID != "ALL") {
            return flow {
                val result = apiInterface.getGameDeals(ApiUtilities.CHEAP_SHARK_BASE_URL+ ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL+"&storeID="+storesID+"&upperPrice=$maxPrice"+"&pageNumber=$pageNumber&sortBy=$sortMode")
                pagesMax = result.headers()["X-Total-Page-Count"]!!
                emit(result)
            }.map {
                it.body()!!
            }
        }else{
            Log.d("Debug","API request link: ${ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL + "upperPrice=$maxPrice" + "&pageNumber=$pageNumber&sortBy=$sortMode"}")

            return flow {
                val result =
                    apiInterface.getGameDeals(ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_STORES_DEALS_URL + "upperPrice=$maxPrice" + "&pageNumber=$pageNumber&sortBy=$sortMode")
                pagesMax = result.headers().get("X-Total-Page-Count")!!
                emit(result)
            }.map {
                it.body()!!
            }
        }
    }
    fun getMaximumPage(): String {
        return pagesMax
    }
    //Games list
    fun getGamesList(): ArrayList<GameItemModel>{
        return gamesList
    }
    fun addToGamesList(newData: List<GameItemModel>){
        gamesList = gamesList.plus(newData) as ArrayList<GameItemModel>
    }
    fun clearGamesList() {
        gamesList.clear()
    }
    fun getCurrentGamesListSize(): Int {
        return gamesList.size
    }
}