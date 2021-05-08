package com.kpa.system

import com.kpa.gift.GiftService
import com.kpa.gift.GiftServiceImpl
import com.kpa.user.UserService
import com.kpa.user.UserServiceImpl

class DHNServiceProviderImpl : DHNServiceProvider {
    override fun getGiftService(): GiftService {
        return GiftServiceImpl()
    }

    override fun getUserService(): UserService {
        return UserServiceImpl()
    }

    companion object {
        init {
            DHNServiceManager.registerProvider(DHNSystem.GIFT_SERVICE_NAME, DHNServiceProviderImpl())
            DHNServiceManager.registerProvider(DHNSystem.USER_SERVICE_NAME, DHNServiceProviderImpl())
        }
    }
}