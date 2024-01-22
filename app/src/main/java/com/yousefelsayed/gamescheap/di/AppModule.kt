package com.yousefelsayed.gamescheap.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.yousefelsayed.gamescheap.api.ApiInterface
import com.yousefelsayed.gamescheap.util.ApiUtilities
import com.yousefelsayed.gamescheap.util.NetworkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesRetrofitInstance(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder().baseUrl(ApiUtilities.CHEAP_SHARK_BASE_URL).addConverterFactory(
            ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient).build()
    }
    @Singleton
    @Provides
    fun providesApiInterface(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
    @Singleton
    @Provides
    fun providesNetworkManager(@ApplicationContext context: Context): NetworkManager = NetworkManager(context)
}