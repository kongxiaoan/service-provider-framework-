服务提供者框架+Class.forName 完成数据组件化

背景
---
> 当项目到一定的量级， 开发人员的增多和功能体系的增大都将是开发面临的难题，组件化思想为大家解决了一些列的问题，针对项目的组件化(图2)，一般沿用的设计模式都是分层架构(图1)的缩体。
图1(分层架构示意图）

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/cd496035ac734d548974576a12e055aa~tplv-k3u1fbpfcp-watermark.image)
图2(组件化示意图）

![image.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/9494b54a0cba4108a6aa6de30533ef92~tplv-k3u1fbpfcp-watermark.image)
### 一、 组件化目的
> 组件化的目的是将项目进行解耦，使得开发可以同步开发多个模块，使得独立的功能复用达到最大化，我们希望组件化后的项目可以做到：

1. 支持大量丰富业务的接入，同时业务之间能够保持清晰的边界，各自可以继续灵活迭代；
2. 用一批统一的中间件去支撑起各种业务的底层功能，保持中间件代码的全面复用；
3. 能够尽量保持对系统的低侵入，尊重原生运行机制以降低后期的维护成本；
4. 在用户设备上尽量体现一个简单客户端的特性，同时特定的业务功能按需获取，保持体积的可控;

> 上述中的任何一点拿出来都可以是一个有力的论点，今天将对第2条 **用一批统一的中间件去支撑起各种业务的底层功能，保持中间件代码的全面复用；** 做一个阐述。

### 二、将各业务中通用的数据进行中间件化
我此篇文章的目的就是结合服务提供者框架将数据的使用管理起来，使之成为独立于各个模块的组件，希望**能在指定的地点初始化(组件化中整体项目的生命周期管控也是个技术活)，在各个业务组件中进行交互而它的操作由组件来完成。、**

目的： 同一管理通用数据组件，各端调用对应方法完成需要功能，让数据组件化，单独跑起来

### 三、服务提供者框架

在介绍服务提供者框架之前，先看几个概念：
#### 3.1 Class.forName()
返回一个给定类或者接口的一个 Class 对象，如果没有给定 classloader， 那么会使用根类加载器。如果 initalize 这个参数传了 true，那么给定的类如果之前没有被初始化过，那么会被初始化,
我们常用到它的功能是在反射中，用来获取一个Class 对象，在这里将使用它的另一个功能，初始化未被初始化的类 ，为什么要这么操作，后续会用到

#### 3.2 服务提供者框架
定义： 多个服务提供者来实现一个服务，系统为客户端的服务提供者提供多个实现，并且
把他们从多个实现中解耦出来。
看完这个定义很多人都是懵逼的状态，比较抽象，不太好理解，可以结合下面的图来看，从中间分开，就是两部分东西，即服务和服务提供者。

服务提供框架有4个组件，依次是服务接口，服务器提供者接口，提供者注册API，服务访问API。
- 服务接口
在服务接口中定义一些提供具体服务的方法，假设我们要提供一个注册登录的服务UserService。那么这个服务接口中肯定有login(),register()方法。我们再去创建这个服务接口的具体实现类去实现login(),register()方法。
- 服务提供者接口
在服务提供者接口里，就是去定义提供什么样子的服务的方法。我们上面创建了一个提供“注册登录”的服务。那么这里我们肯定要去定义一个能获取“注册登录”的服务的方法，假设是getUserService(),返回类型是UserService。然后在去创建服务提供者接口的具体实现类去这个getUserService()，那么我们怎么去实现呢？我们只需要返回一个UserService的具体实现类即可。
- 提供者注册API
其实是服务提供者接口的具体实现类里面去注册这个API，在类中的静态初始化块中去注册API，因为你只有注册了API，才能享有享受服务的权利。这些注册过的服务集中交给ServiceManager管理。
- 服务访问API
既然已经注册了API，那么我们可以向ServiceManager申请具体的服务，可以获得具体服务的实例，就可以调用服务里面的方法。服务访问API是“灵活的静态工厂”，它构成了服务提供者框架的基础。
具体先看一个例子(用项目中的礼物举例)
##### 3.2.1 礼物服务接口，用来向外界提供可操作的方法
针对多端(即多应用)场景中，数据entity可能各不相同，此情况我们可以使用适配器模式将数据进行适配，保证数据操作entity和应用数据库(映射型数据库，下同)表entity保持一致性，并且将数据库entity分离，使得数据库entity和业务entity 职责单一
```kotlin
interface GiftService {
    fun saveGift()// adapter 适配者模式 对数据进行统一 entry
    fun deleteGift()
    fun clearGift()
    fun updateGift()
    fun getGift()
    fun selectGift()
}
```

##### 3.2.2 服务具体实现类, 在服务提供者中暴露给使用者
```kotlin
class GiftServiceImpl: GiftService {
    override fun saveGift() {
        println("保存数据")
    }

    override fun deleteGift() {
    }

    override fun clearGift() {
    }

    override fun updateGift() {
    }

    override fun getGift() {
        println("获取了所有的礼物")
    }

    override fun selectGift() {
    }
}
```

服务提供者的前半部分已经OK了，
##### 3.2.3 服务提供者接口
```kotlin
/**
 * 服务提供者
 */
interface GiftProvider {
    fun getGiftService(): GiftService
}
```
##### 3.2.4 服务提供者接口的具体实现

> 在这个类中，我们将注册一个服务到服务提供者，**这里写在companion object中，从字节码可以看出其实是写在了static 代码块中，所以当使用Class.forName加载类时，此注册将会被加载。**
```kotlin
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
```
##### 3.2.5 服务提供者管理类
```kotlin
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
```
##### 3.2.5 使用

> 一般在Android开发中会在Application中注册，其他位置使用即可
初始化：（Application中）
```kotlin
Class.forName("com.kpa.system. GiftProviderImpl")
```
使用：使用服务提供者管理类获取对应服务，操作对应的方法，在开发中我们会统一它的操作
```kotlin
val giftService = GiftManager.getService(RealUSystem.GIFT_SERVICE_NAME)
giftService.saveGift()
giftService.getGift()
```

### 四、通用类型的数据组件化方案
> 使用服务提供者框架进行统一封装，**说到底其实只需要提供统一的服务提供者来管理各个数据服务即可
管理各个数据服务，一般由负责数据组件化的开发者维护(权限保护)**
- 服务提供者
```kotlin
interface DHNServiceProvider {

    fun getGiftService(): GiftService

    // 组件 数据组件 可复用的目的， user level
    // 认为是组件的
    fun getUserService(): UserService

}
```
- 服务提供者具体实现
```kotlin
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
```
- 服务管理者
```kotlin
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
```

### 至此，完成了使用服务提供者框架来组件化数据的操作，有人可能有以下疑问：
- 添加数据类型，需要修改提供者类，是不是不方便扩展？
> 对于数据组件化来讲，这个操作是微不足道的，我们组件化开发中有明确的分工，负责数据组件化的开发者维护即可
- 对于某些组件不需要所有的数据类型，全部注册服务，岂不是浪费内存，增加编译时间吗？
> 这里只是讲解怎么去使用服务提供者完成数据组件化，对于这个问题可以使用Builder 模式对其进行配置，使之用则加载反之则弃。

### 总结
> 我们将数据组件化后变成一个可复用的中间件，不仅可以内聚数据管理，并且可以解决不同组件中的复用问题，所以数据组件化是必不可少的组件化操作。

Demo 地址 https://github.com/kongxiaoan/service-provider-framework-.git
