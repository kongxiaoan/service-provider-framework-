package com.kpa.gift

class GiftProviderImpl: GiftProvider {
    override fun getGiftService(): GiftService {
        return GiftServiceImpl()
    }

    companion object {
        init {
            //JDBC
            GiftManager.registerProvider(RealUSystem.GIFT_SERVICE_NAME, GiftProviderImpl())
        }
    }
}