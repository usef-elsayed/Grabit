package com.yousefelsayed.gamescheap.repository

import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.model.GamesArrayModel
import com.yousefelsayed.gamescheap.util.ApiUtilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeFragmentRepository @Inject constructor(private val apiInterface: ApiInterface) {

    suspend fun getSteamAndEpicGames(): Flow<GamesArrayModel>{
        return flow {
            emit(apiInterface.getSteamAndEpicGames(
                ApiUtilities.CHEAP_SHARK_BASE_URL + ApiUtilities.CHEAP_SHARK_STEAM_EPIC_GAMES))
        }.map { it.body()!! }
    }
}