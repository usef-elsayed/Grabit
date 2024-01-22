package com.yousefelsayed.gamescheap.model

import com.google.gson.annotations.SerializedName

data class GameItemModel(
    @SerializedName("dealID")
    val dealID: String,
    @SerializedName("dealRating")
    val dealRating: String,
    @SerializedName("gameID")
    val gameID: String,
    @SerializedName("internalName")
    val internalName: String,
    @SerializedName("isOnSale")
    val isOnSale: String,
    @SerializedName("lastChange")
    val lastChange: Int,
    @SerializedName("metacriticLink")
    val metacriticLink: String,
    @SerializedName("metacriticScore")
    val metacriticScore: String,
    @SerializedName("normalPrice")
    val normalPrice: String,
    @SerializedName("releaseDate")
    val releaseDate: Int,
    @SerializedName("salePrice")
    val salePrice: String,
    @SerializedName("savings")
    val savings: String,
    @SerializedName("steamAppID")
    val steamAppID: String,
    @SerializedName("steamRatingCount")
    val steamRatingCount: String,
    @SerializedName("steamRatingPercent")
    val steamRatingPercent: String,
    @SerializedName("steamRatingText")
    val steamRatingText: String,
    @SerializedName("storeID")
    val storeID: String,
    @SerializedName("thumb")
    val thumb: String,
    @SerializedName("title")
    val title: String
)