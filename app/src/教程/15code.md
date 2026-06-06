好的，开始实现第15天！RecyclerView 列表展示所有注册用户。

现在我已经全面了解了项目现状。项目使用版本目录（`libs.versions.toml`）管理依赖，且没有开启 ViewBinding，所以适配器需要用 `findViewById` 方式。开始逐步实现！

### 步骤1：添加 RecyclerView 依赖

### 步骤2：创建 item_user.xml 列表条目布局

### 步骤3：创建 User.kt 数据实体类

### 步骤4：创建 UserAdapter.kt 适配器

### 步骤5：修改 activity_main.xml 添加 RecyclerView

### 步骤6：修改 MainActivity.kt 读取SP数据绑定列表

### 步骤7：检查 lint 错误

全部完成！没有 lint 错误。下面总结一下今天完成的所有内容：

---

## ✅ 第15天 RecyclerView 实现完成！

### 📁 新建文件（3个）

| 文件 | 作用 |
|------|------|
| `item_user.xml` | 列表单条目布局（账号+性别+爱好三行文字） |
| `User.kt` | 数据实体类，封装一条用户数据 |
| `UserAdapter.kt` | 适配器，数据 ↔ 列表的桥梁（含点击事件） |

### 📝 修改文件（3个）

| 文件 | 改动 |
|------|------|
| `libs.versions.toml` | 新增 `recyclerview = "1.3.2"` 版本号和库声明 |
| `build.gradle.kts` | 新增 `implementation(libs.androidx.recyclerview)` 依赖 |
| `activity_main.xml` | 底部新增 RecyclerView 列表容器 + 分隔标题 |
| `MainActivity.kt` | 读取 USER_LIST 全部数据 → 装载进列表 → 条目点击弹出提示 |

### 🔑 核心知识点回顾

1. **RecyclerView三要素**：
    - **RecyclerView** = 列表容器（放页面上）
    - **Item布局** = 单条数据长什么样（`item_user.xml`）
    - **Adapter适配器** = 数据桥梁（`UserAdapter.kt`）

2. **Adapter三个必须重写的方法**：
    - `onCreateViewHolder` → 创建条目视图
    - `onBindViewHolder` → 把数据填进控件
    - `getItemCount` → 返回数据总数

3. **数据流转过程**：
   ```
   SharedPreferences(USER_LIST) 
     → 读取所有key(账号) 
     → 拆分value(密码|性别|爱好) 
     → 封装成User对象 
     → 加入MutableList 
     → 适配器显示到RecyclerView
   ```

4. **notifyDataSetChanged()**：数据变化后必须调用，否则列表不刷新！

### 🚀 使用方式

1. 先注册若干账号（带性别和爱好）
2. 登录进入主页，下方自动展示**所有注册用户**的竖向列表
3. 点击某条用户，弹出 `Toast` 提示选中的账号
4. 数据来自本地 SharedPreferences，重启APP数据仍在

> ⚠️ 添加依赖后请点击 Android Studio 右上角的 **Sync Now** 同步项目！