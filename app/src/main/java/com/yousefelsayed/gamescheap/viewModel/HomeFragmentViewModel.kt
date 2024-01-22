package com.yousefelsayed.gamescheap.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yousefelsayed.gamescheap.api.Resource
import com.yousefelsayed.gamescheap.model.GamesArrayModel
import com.yousefelsayed.gamescheap.repository.HomeFragmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
data class HomeFragmentViewModel @Inject constructor(private val homeFragmentRepository: HomeFragmentRepository): ViewModel() {

    private val _steamEpicGames = MutableStateFlow<Resource<GamesArrayModel>>(Resource.loading())
    val steamEpicGames get() = _steamEpicGames

    fun getSteamAndEpicGames(){
        viewModelScope.launch(Dispatchers.IO) {
            homeFragmentRepository.getSteamAndEpicGames().catch { error ->
                _steamEpicGames.value = Resource.error(error.message!!)
            }
            .collect{ result ->
                    _steamEpicGames.value = Resource.success(result)
                }
        }
    }
}