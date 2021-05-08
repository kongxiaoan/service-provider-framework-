package com.kpa.system

import com.kpa.gift.GiftService
import com.kpa.user.UserService

//dhn-android-data
interface DHNServiceProvider {

    fun getGiftService(): GiftService

    // 组件 数据组件 可复用的目的， user level
    // 认为是组件的
    fun getUserService(): UserService

}