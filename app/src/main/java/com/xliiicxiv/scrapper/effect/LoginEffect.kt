package com.xliiicxiv.scrapper.effect

import com.xliiicxiv.scrapper.route.Route

interface LoginEffect {

    data class Navigate(val route: Route) : LoginEffect

}