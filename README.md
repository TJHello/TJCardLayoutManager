# TJCardLayoutManager [![](https://jitpack.io/v/TJHello/TJCardLayoutManager.svg)](https://jitpack.io/#TJHello/TJCardLayoutManager)
卡片滑动LayoutManager。使用该库，可以让你简单做出卡片滑动效果。

- - -

* **Step 1. 添加Jitpack仓库到你的项目build.gradle**
  ```groovy
    allprojects {
	    repositories {
		    ...
		    maven { url 'https://jitpack.io' }
	    }
    }
  ```
* **Step 2. 添加远程库到app-build.gradle**
  ```groovy
    dependencies {
        implementation 'com.github.TJHello:TJCardLayoutManager:1.0.2'
    }
  ```
* **step 3. 代码接入**

```kotlin
    //注意，我已经在TJCardFlingManager中为RecyclerView设置了LayoutManner，请不要重新设置。
    //TJCardFlingManager只是一个二次封装，有其他需求的伙伴可查看里面的代码，自定义自己的管理器。
    private lateinit var  cardFlingManager : TJCardFlingManager
    override fun onCreate(savedInstanceState: Bundle?) {
        ...
        cardFlingManager = TJCardFlingManager(recyclerView,OnTJCardViewListener())
        ...
    }
```
### 效果预览
![image](https://gitee.com/tjbaobao/TJCardLayoutManager/attach_files/download?i=194213&u=http%3A%2F%2Ffiles.git.oschina.net%2Fgroup1%2FM00%2F05%2FFE%2FPaAvDFwbfjaAPQitADyCgsT9sOw607.gif%3Ftoken%3Dd1473a3937e8b7f5bb622f4591c700b3%26ts%3D1545305654%26attname%3Duntitled.gif)