package com.yousefelsayed.gamescheap.di

import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.repository.GamesFragmentRepository
import com.yousefelsayed.gamescheap.repository.HomeFragmentRepository
import com.yousefelsayed.gamescheap.repository.SearchFragmentRepository
import com.yousefelsayed.gamescheap.viewModel.GamesFragmentViewModel
import com.yousefelsayed.gamescheap.viewModel.HomeFragmentViewModel
import com.yousefelsayed.gamescheap.viewModel.SearchFragmentViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    //ViewModel provider
    @Provides
    fun providesHomeFragmentViewModel(homeFragmentRepository: HomeFragmentRepository): HomeFragmentViewModel = HomeFragmentViewModel(homeFragmentRepository)
    @Provides
    fun providesGamesFragmentViewModel(gamesFragmentRepository: GamesFragmentRepository): GamesFragmentViewModel = GamesFragmentViewModel(gamesFragmentRepository)
    @Provides
    fun providesSearchFragmentViewModel(searchFragmentRepository: SearchFragmentRepository): SearchFragmentViewModel = SearchFragmentViewModel(searchFragmentRepository)

    //Repository provider
    @Provides
    fun providesHomeFragmentRepository(apiInterface: ApiInterface): HomeFragmentRepository = HomeFragmentRepository(apiInterface)
    @Provides
    fun providesGamesFragmentRepository(apiInterface: ApiInterface): GamesFragmentRepository = GamesFragmentRepository(apiInterface)
    @Provides
    fun providesSearchFragmentRepository(apiInterface: ApiInterface): SearchFragmentRepository = SearchFragmentRepository(apiInterface)

}