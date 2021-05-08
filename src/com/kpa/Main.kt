package com.kpa

import com.kpa.gift.GiftManager
import com.kpa.gift.GiftService
import com.kpa.gift.RealUSystem
import com.kpa.system.DHNServiceManager
import com.kpa.system.DHNSystem
import com.kpa.user.UserService

fun liveRoom() {
    val service = GiftManager.getService(RealUSystem.GIFT_SERVICE_NAME)
    service.getGift()
}

fun main() {
    //1. 初始化 application
    Class.forName("com.kpa.system.DHNServiceProviderImpl")

//    val giftService = GiftManager.getService(RealUSystem.GIFT_SERVICE_NAME)
//    giftService.saveGift()
//    giftService.getGift()
//    liveRoom()
//    testDHNService()
    testUser()
}

fun testUser() {
    val server = DHNServiceManager.getServer<UserService>(DHNSystem.USER_SERVICE_NAME)
    server?.saveUser()
    println(server?.getUserId())
}

fun testDHNService() {
    Class.forName("com.kpa.system.DHNServiceProviderImpl")
    val server = DHNServiceManager.getServer<GiftService>(DHNSystem.GIFT_SERVICE_NAME)
    server?.saveGift()
    server?.getGift()
}