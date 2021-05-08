package com.kpa.gift

import java.util.concurrent.ConcurrentHashMap

object GiftManager {
    private val provides = ConcurrentHashMap<String, GiftProvider>()

    fun registerProvider(name: String, provider: GiftProvider) {
        provides[name] = provider
    }

    fun getService(name: String): GiftService {
        val provider = provides[name]
        if (provider == null) {
            throw IllegalArgumentException("No provider registered with name = $name")
        }
        return provider.getGiftService()
    }
}