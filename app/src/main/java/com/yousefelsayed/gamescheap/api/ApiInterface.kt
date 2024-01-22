package com.yousefelsayed.gamescheap.api

import com.yousefelsayed.gamescheap.model.GamesArrayModel
import com.yousefelsayed.gamescheap.model.SearchArrayModel
import com.yousefelsayed.gamescheap.model.StoresArrayModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiInterface {
    @GET
    suspend fun getGames(@Url url:String): Response<GamesArrayModel>
    @GET
    suspend fun getStores(@Url url:String): Response<StoresArrayModel>
    @GET
    suspend fun getSearchResults(@Url url:String): Response<SearchArrayModel>
    @GET
    suspend fun getSteamAndEpicGames(@Url url:String): Response<GamesArrayModel>
    @GET
    suspend fun getGameDetails(@Url url:String): Response<String>
    @GET
    suspend fun getGameDeals(@Url url:String): Response<GamesArrayModel>
}