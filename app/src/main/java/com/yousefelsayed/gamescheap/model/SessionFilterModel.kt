package com.yousefelsayed.gamescheap.model

class SessionFilterModel {
    private val storesArray = arrayListOf("ALL")
    private var sortMode: String = "Deal Rating"
    var maxPrice: String = "50"
    var pageNumber = 0
    //Sort Mode
    fun getSortMode(): String {
        return sortMode
    }
    fun setSortMode(sort: String) {
        sortMode = sort
    }
    //Stores
    fun addToStoresArray(value: String): Boolean{
        storesArray.remove("ALL")
        storesArray.add(value)
        return true
    }
    fun removeFromStoresArray(value: String): Boolean{
        storesArray.remove(value)
        if (storesArray.size == 0){
            storesArray.add("ALL")
        }
        return true
    }
    fun getStoresArrayInString(): String{
        return storesArray.joinToString(",")
    }
    fun storesArrayContains(searchValue: String): Boolean{
        return storesArray.contains(searchValue)
    }

}