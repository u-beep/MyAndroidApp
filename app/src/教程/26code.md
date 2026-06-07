现在我需要实现第26天的SharedPreferences封装+记住密码+自动登录功能。让我先创建一个待办事项列表来跟踪这个任务：

## 1. 创建SPUtil.kt工具类

首先创建utils包（如果不存在的话），然后创建SPUtil.kt文件：





## 2. 创建MyApp.kt自定义Application类



## 3. 更新AndroidManifest.xml注册MyApp

我需要修改application标签的android:name属性：



## 4. 修改LoginActivity.kt布局文件，添加CheckBox控件

首先查看当前的登录页面布局：

我看到布局已经有CheckBox了，但是需要添加自动登录的CheckBox和调整布局。我需要修改这个布局：

我需要给LinearLayout添加ID：



## 5. 修改LoginActivity.kt，实现记住密码和自动登录逻辑

让我查看当前的LoginActivity.kt文件：

我需要修改LoginActivity.kt来实现新的SP工具类集成。首先查看完整的文件内容：

现在我需要修改LoginActivity.kt来使用新的SPUtil工具类并添加自动登录功能：

让我重新读取文件并找到正确的位置：

现在我将重写LoginActivity.kt：



## 6. 修改SettingActivity.kt布局文件，添加字体大小和夜间模式控件

首先查看当前的setting.xml文件：

我需要修改布局文件，添加字体大小和夜间模式控件。我会在现有音量调节区域之后、退出登录按钮之前添加：



## 7. 修改SettingActivity.kt，实现字体大小和夜间模式功能

现在修改SettingActivity.kt来添加字体大小和夜间模式功能：

我需要修改SettingActivity.kt来添加新的控件和功能：

让我查看SettingActivity.kt的完整文件，然后进行修改：

我将重写SettingActivity.kt来整合新的功能：



## 8. 测试编译和运行

现在让我测试编译整个应用：

有编译错误，`utils`包引用有问题。我需要修复这个导入问题：

我需要直接修改所有的`utils.SPUtil`调用为`SPUtil`：

我看到还有`utils.`前缀需要修复。让我继续修复剩下的引用：

我需要修复所有的`utils.SPUtil`引用。让我查看文件内容并修复：

我看到文件中还有一些`utils.SPUtil`引用。让我修复所有的引用：

继续修复剩下的引用：

继续修复：

现在修复字体大小和夜间模式部分的引用：

现在我也需要修复LoginActivity.kt中的引用：

继续修复LoginActivity.kt中的剩余引用：

现在测试编译：

SPUtil类没有被正确导入。因为SPUtil是在`utils`包中，我需要检查LoginActivity.kt和SettingActivity.kt是否使用了正确的导入语句。首先查看SPUtil的位置：

SPUtil在utils包中。我需要在LoginActivity.kt和SettingActivity.kt中添加导入：

让我查看LoginActivity.kt的开头部分：

我需要在文件末尾添加导入：

我需要在AppCompatActivity导入后添加SPUtil导入。由于SPUtil和LoginActivity在同一个包的不同文件夹中，我需要使用完全限定名或者导入。让我检查SPUtil的文件路径和包名：

SPUtil在`com.example.myapplication.utils`包中。我在LoginActivity.kt和SettingActivity.kt中使用完全限定名来调用。我需要添加导入语句。让我编辑LoginActivity.kt：

现在编辑SettingActivity.kt：

现在还需要检查MineFragment.kt，它可能还在使用老的SharedPreferences。让我查看：

我需要更新MineFragment.kt来使用新的SPUtil类：

现在再次测试编译：

MyApp.kt中也有`utils.SPUtil`引用。我需要修复它：



现在再次测试编译：

太好了！编译成功了。现在让我检查是否有其他编译问题或缺少的导入：

现在让我测试运行应用：



太棒了！所有任务都已完成。现在让我总结一下第26天教程的实现情况：

## ✅ Android第26天完整版｜SharedPreferences封装+记住密码+自动登录实战 - 已完成

### 已实现的功能：

#### 1. **SPUtil工具类（单例模式）** ✅
- 创建了`utils/SPUtil.kt`工具类
- 封装了字符串、布尔值、整数等常用数据类型的存取方法
- 实现了单例模式，全局统一管理SP文件`user_config`
- 提供了删除单个键和清空所有数据的方法

#### 2. **MyApp自定义Application** ✅
- 创建了`MyApp.kt`继承自`Application`
- 在`onCreate()`方法中初始化`SPUtil.init(this)`
- 在`AndroidManifest.xml`中注册了`MyApp`

#### 3. **登录页面改造** ✅
- 修改了`activity_login.xml`布局，添加了两个CheckBox（记住密码、自动登录）
- 重写了`LoginActivity.kt`，实现了新的记住密码和自动登录逻辑：
    - 页面初始化时读取SP状态，回填账号密码和勾选状态
    - 支持自动登录：如果勾选了记住密码和自动登录，APP启动时直接跳转到主页
    - 登录时根据勾选状态保存或清除账号密码
    - 使用了统一的SPUtil工具类替代原有的SharedPreferences

#### 4. **设置页面拓展** ✅
- 修改了`activity_setting.xml`布局，添加了：
    - 字体大小调节：TextView显示当前字体大小 + SeekBar拖动条（12-22sp范围）
    - 夜间模式开关：CheckBox控制夜间模式
- 更新了`SettingActivity.kt`，实现了：
    - 字体大小调节功能，保存到SP
    - 夜间模式开关，保存到SP
    - 退出登录时清空登录信息和自动登录状态

#### 5. **MineFragment退出登录逻辑更新** ✅
- 更新了`MineFragment.kt`中的退出登录功能，使用新的SPUtil工具类

### 技术实现要点：

1. **统一SP管理**：所有SharedPreferences操作都通过`SPUtil`工具类完成
2. **键名常量**：在`LoginActivity`中定义了统一的键名常量，防止写错
3. **自动登录逻辑**：需要同时满足"记住密码"和"自动登录"两个条件
4. **配置持久化**：字体大小和夜间模式设置都会永久保存
5. **清除逻辑**：退出登录时正确清除相关SP数据

### 运行流程效果：

1. **首次登录**：输入账号密码，勾选记住密码+自动登录→登录进首页
2. **关闭APP重进**：自动跳过登录页，直接进入主页（自动登录生效）
3. **取消记住密码**：登录后不再保存账号，下次打开输入框空白
4. **退出登录**：设置页点退出，清空自动登录，下次启动停留在登录页
5. **设置配置永久保存**：修改字体大小、夜间模式，关闭APP再次打开配置不变

### 今日必背知识点：

1. **SharedPreferences适合少量配置数据持久化**，大数据用SQLite
2. **`apply()`异步保存**推荐；`commit()`同步阻塞主线程，极少使用
3. **记住密码**：勾选→SP存账号密码；取消勾选→remove删除key
4. **自动登录依赖「记住密码+自动登录双勾选」**，缺一不可
5. **SP封装工具类**，项目统一管理key，便于后期维护修改

## 📱 测试建议：

1. 运行应用，测试登录页面的记住密码和自动登录功能
2. 在设置页面调节字体大小和开关夜间模式，关闭应用后重新打开查看是否保存
3. 测试退出登录功能，确保自动登录状态被正确清除

所有功能已成功实现，代码编译通过，可以正常使用！🎉

**需要第27天完整版教程吗？**