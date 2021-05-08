package com.kpa.gift


/**
 * 服务接口
 */
interface GiftService {
    fun saveGift()// adapter 适配者模式 对数据进行统一 entry

    fun deleteGift()

    fun clearGift()

    fun updateGift()

    fun getGift()

    fun selectGift()
}