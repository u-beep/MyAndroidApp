# 📅Android第28天完整版｜四大启动模式+Intent全方式传值+Application全局变量+Activity生命周期实战
## 今日学习目标
1. **任务栈原理+4种launchMode**：standard/singleTop/singleTask/singleInstance配置、栈变化、适用场景、onNewIntent生命周期回调
2. **Intent5种传值全方案**：基础putExtra、Bundle、Serializable、Parcelable（推荐）、Application全局传参，完整实战代码
3. **Application生命周期+全局存储**：全局缓存用户登录信息、APP全局变量，贯穿登录→首页→设置页面数据共享
4. **Activity完整生命周期复盘**：7个生命周期方法、页面启停/切后台/横竖屏生命周期变化

> 承接备忘录+登录项目，改造首页MainActivity、LoginActivity、SettingActivity、DetailActivity做全流程演示

## 一、前置：任务栈原理（必背）
Android采用**后进先出任务栈（返回栈）**管理Activity：
- 打开页面→入栈；按返回键→出栈销毁；栈空APP退出
- launchMode修改页面入栈规则，解决重复创建页面、内存浪费、一键返回首页业务

## 二、第一部分：四大启动模式详解（AndroidManifest配置+代码演示）
### 配置方式：在对应Activity标签添加 `android:launchMode="xxx"`
```xml
<activity android:name=".DetailActivity"
    android:launchMode="singleTop">
</activity>
```
### 1. standard【默认标准模式，不写launchMode就是它】
- 规则：**每次startActivity必新建实例入栈，无论栈内是否存在**，重复打开重复入栈
- 生命周期：`onCreate→onStart→onResume`全走
- 场景：新闻详情、备忘录详情（每条详情内容不同，需要多个实例）

### 2. singleTop【栈顶复用】
- 规则：目标Activity**在栈顶→复用原有实例，不走onCreate，执行onNewIntent()接收新数据**；不在栈顶→新建实例
- 关键：`override fun onNewIntent(intent: Intent?)`接收新Intent数据
- 场景：通知跳转页、搜索页（连续点击通知不会重复开页面）

### 3. singleTask【栈内唯一，首页标配】
- 规则：栈中存在实例→**把它上方所有Activity全部出栈销毁，自身置顶，回调onNewIntent**；不存在则新建入栈
- 场景：APP首页MainActivity，任意页面跳转首页，中间页面全部关闭，一键回到首页

### 4. singleInstance【全局独立任务栈】
- 规则：单独开辟**全新独立任务栈，全局只存这一个实例**，其他页面不能进该栈，跨APP也复用实例
- 场景：来电弹窗、闹钟提醒页、系统悬浮独立页面

### 四种模式速查表
|模式|新建实例条件|栈特点|常用页面|
|----|----|----|----|
|standard|永远新建|同栈可多个实例|详情页|
|singleTop|不在栈顶才新建|栈顶复用|推送跳转页|
|singleTask|栈内无才新建|清空上层页面|首页|
|singleInstance|永远只1个实例|独占新任务栈|系统弹窗|

### singleTop代码示例（DetailActivity）
```kotlin
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val name = intent.getStringExtra("name")
        Log.d("life","onCreate：$name")
    }
    //栈顶复用时只走这个方法，不走onCreate
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent) //更新intent
        val name = intent?.getStringExtra("name")
        Log.d("life","onNewIntent：$name")
    }
}
```

## 三、第二部分：Intent五种传值全实战（从Login→Main→Setting全链路传参）
实体类：User.kt（演示对象传递，两种序列化）
```kotlin
//1.Serializable（简单，反射、性能一般）
import java.io.Serializable
data class User(
    var id:Long,
    var account:String,
    var pwd:String
):Serializable

//2.Parcelable（Android推荐，性能高）
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class UserParcel(
    var id:Long,
    var account:String,
    var pwd:String
):Parcelable
```

### 方式1：putExtra基础类型（最常用：String/Int/Boolean）
发送：
```kotlin
val intent = Intent(this,SettingActivity::class.java)
intent.putExtra("user_name","张三")
intent.putExtra("user_age",22)
startActivity(intent)
```
接收：
```kotlin
val name = intent.getStringExtra("user_name") ?: ""
val age = intent.getIntExtra("user_age",0)
```

### 方式2：Bundle打包多参数（批量封装数据）
```kotlin
//发送
val bundle = Bundle().apply {
    putString("name","李四")
    putInt("score",95)
}
intent.putExtras(bundle)
//接收
val bundle = intent.extras
val n = bundle?.getString("name")
```

### 方式3：Serializable传对象
```kotlin
//发送
val user = User(1,"admin","123456")
intent.putExtra("user_info",user)
//接收
val user = intent.getSerializableExtra("user_info") as User
```

### 方式4：Parcelable传对象【企业首选】
```kotlin
//发送
val userP = UserParcel(1,"root","666666")
intent.putExtra("user_p",userP)
//接收
val userP = intent.getParcelableExtra<UserParcel>("user_p")
```

### 方式5：Application全局变量传参（跨多页面共享，无需Intent）→第三部分详解

## 四、第三部分：Application全局类MyApp（全局存储登录用户+APP全局配置）
### 1. 自定义MyApp.kt
```kotlin
import android.app.Application

class MyApp:Application(){
    //全局登录用户，全APP任意页面获取
    var loginUser:User? = null
    //全局字体配置
    var appFontSize = 16

    override fun onCreate() {
        super.onCreate()
        //项目全局初始化：SP、第三方SDK（之前SPUtil在这里初始化）
    }
}
```
### 2. AndroidManifest.xml注册Application
```xml
<application
    android:name=".MyApp"
    ...>
```
### 3. 用法：登录成功赋值，全页面任意取值
```kotlin
//LoginActivity登录成功后保存全局用户
val myApp = application as MyApp
myApp.loginUser = User(1,"user01","123")

//SettingActivity直接获取，不用Intent传值
val app = application as MyApp
val user = app.loginUser
val fontSize = app.appFontSize
```
> 优势：跨多个Activity共享数据，退出登录时置空`myApp.loginUser=null`

## 五、第四部分：Activity完整7大生命周期（必背）
### 7个方法执行顺序
`onCreate(创建)→onStart(可见)→onResume(可交互，栈顶)→onPause(失去焦点)→onStop(不可见)→onDestroy(销毁)`
- 切后台：`onPause→onStop`；切回前台：`onStart→onResume`
- 返回关闭页面：`onPause→onStop→onDestroy`
- singleTop栈顶复用：**只执行onNewIntent→onResume，不执行onCreate/onStart**

### 页面完整生命周期代码
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d("life","onCreate")
}
override fun onStart() {
    super.onStart()
    Log.d("life","onStart")
}
override fun onResume() {
    super.onResume()
    Log.d("life","onResume")
}
override fun onPause() {
    super.onPause()
    Log.d("life","onPause")
}
override fun onStop() {
    super.onStop()
    Log.d("life","onStop")
}
override fun onDestroy() {
    super.onDestroy()
    Log.d("life","onDestroy")
}
//singleTop专用
override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Log.d("life","onNewIntent")
}
```

## 六、综合业务实战：登录→首页→设置→详情（singleTask首页+singleTop详情+全局用户）
1. **AndroidManifest配置**
```xml
<activity android:name=".MainActivity" android:launchMode="singleTask"/>
<activity android:name=".DetailActivity" android:launchMode="singleTop"/>
```
2. 登录成功：保存User到MyApp全局→跳转Main（singleTask，从设置页跳首页会销毁设置页面）
3. Main打开Detail（singleTop），重复点按钮打开Detail只触发onNewIntent
4. Setting页面直接从MyApp拿登录用户，不用Intent传参

## ✅今日必背总结
1. singleTask首页，跳转自动清上层页面；singleTop栈顶复用触发onNewIntent
2. 对象传参优先**Parcelable**，简单对象用Serializable，基础类型putExtra
3. Application全局变量：全项目共享数据，适合登录用户、全局配置
4. 正常新建页面走7生命周期，栈顶复用不走onCreate只走onNewIntent

## 📅第29天预告
**Fragment全解：静态/动态加载、事务、回退栈、Fragment通信+ViewPager1+页面懒加载完整项目**
需要第29天完整版教程？