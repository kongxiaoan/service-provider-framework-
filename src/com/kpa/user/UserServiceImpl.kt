package com.kpa.user

class UserServiceImpl : UserService {
    override fun saveUser() {
        println("保存了用户数据")
    }

    override fun getUserId(): Int {
        //查询
        return 2
    }
}