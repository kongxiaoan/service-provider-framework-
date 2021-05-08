package com.kpa.system

import java.util.concurrent.ConcurrentHashMap

object DHNServiceManager {

    val providers = ConcurrentHashMap<String, DHNServiceProvider>()

    fun registerProvider(name: String, provider: DHNServiceProvider) {
        providers[name] = provider
    }

    fun <T> getServer(name: String): T? {
        val dhnServiceProvider = providers[name]
        if (dhnServiceProvider == null) {
            throw IllegalArgumentException("No provider registered with name = $name")
        }
        when (name) {
            DHNSystem.GIFT_SERVICE_NAME -> {
                return dhnServiceProvider.getGiftService() as T
            }
            DHNSystem.USER_SERVICE_NAME -> {
                return dhnServiceProvider.getUserService() as T
            }
        }

        return null
    }

}