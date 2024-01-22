package com.yousefelsayed.gamescheap.util

public object ApiUtilities {
    //Cheap Shark Api
    public const val CHEAP_SHARK_BASE_URL = "https://www.cheapshark.com"
    public const val CHEAP_SHARK_STORES_DEALS_URL = "/api/1.0/deals?"
    public const val CHEAP_SHARK_TOP_GAMES_URL = "/api/1.0/deals?upperPrice=500&pageSize=15"
    public const val CHEAP_SHARK_STEAM_EPIC_GAMES = "/api/1.0/deals?storeID=1,25&upperPrice=500&pageSize=15"
    public const val CHEAP_SHARK_SEARCH = "/api/1.0/games?title="
    public const val CHEAP_SHARK_STORES_URL = "/api/1.0/stores"
    public const val CHEAP_SHARK_REDIRECT_LINK = "https://www.cheapshark.com/redirect?dealID="
    //Steam Api
    public const val STEAM_BASE_URL = "https://store.steampowered.com/api"
    public const val STEAM_GAME_DETAILS_URL = "/appdetails?appids="
}