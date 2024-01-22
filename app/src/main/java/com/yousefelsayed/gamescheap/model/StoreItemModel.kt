package com.yousefelsayed.gamescheap.model

data class StoreItemModel(
    val images: ImageModel,
    val isActive: Int,
    val storeID: String,
    val storeName: String
){
}