这是一个大重构！把原来的主页拆成3个Fragment + 底部导航。我需要仔细规划，保留所有现有功能。

先完整读取现有的关键文件，确保不遗漏功能：

好，现在我对所有现有功能了解清楚了。开始逐步创建 Fragment 文件：

现在创建 UserListFragment：

现在创建 MineFragment：

现在重写 `activity_main.xml`，完全替换为底部导航结构：

现在需要创建底部导航的颜色选择器和图标资源：

现在创建6个矢量图标（蓝色和灰色各3个）：

现在重写 `MainActivity.kt`：

现在需要给 `HomeFragment` 添加 `showImage` 方法，供 `MainActivity` 回传图片：

现在检查 lint 错误：

✅ **第24天全部完成！** 零 lint 错误，项目已重构为 Fragment + 底部导航架构！

---

## 📋 第24天完成清单

### 新建文件（11个）

| 文件 | 说明 |
|---|---|
| `fragment_home.xml` | 首页布局：欢迎信息 + 用户信息 + 图片 + 宠物容器 |
| `HomeFragment.kt` | 首页逻辑：显示账号信息、初始化宠物、接收相册图片 |
| `fragment_userlist.xml` | 用户列表布局：RecyclerView |
| `UserListFragment.kt` | 用户列表逻辑：数据库加载 + 长按删除弹窗 |
| `fragment_mine.xml` | 我的布局：备忘录 + 设置 + 相册 + 退出按钮 |
| `MineFragment.kt` | 我的逻辑：跳转备忘录/设置、调用相册、退出登录 |
| `tab_text_color.xml` | 底部导航文字颜色选择器（选中蓝/未选中灰） |
| `tab_icon_home.xml` | 首页Tab图标选择器 |
| `tab_icon_user.xml` | 用户列表Tab图标选择器 |
| `tab_icon_mine.xml` | 我的Tab图标选择器 |
| `ic_home/blue/gray`, `ic_user/blue/gray`, `ic_mine/blue/gray` | 6个矢量图标 |

### 重写文件（2个）

| 文件 | 改动 |
|---|---|
| `activity_main.xml` | 完全重写：FrameLayout容器 + RadioGroup底部导航 |
| `MainActivity.kt` | 完全重写：Fragment切换逻辑 + 保留权限申请方法 |

---

## 🔑 核心架构

```
MainActivity（容器）
├── FrameLayout (fl_content) → Fragment 动态替换区域
│   ├── HomeFragment（首页：欢迎+宠物+图片）
│   ├── UserListFragment（用户列表：RecyclerView+删除）
│   └── MineFragment（我的：备忘录+设置+相册+退出）
└── RadioGroup (rg_tab) → 底部三栏导航
    ├── RadioButton「首页」
    ├── RadioButton「用户列表」
    └── RadioButton「我的」
```

## 🔑 核心知识点

| 概念 | 说明 |
|---|---|
| **Fragment** | 内嵌在 Activity 里的子页面，同一屏幕切换 |
| **supportFragmentManager** | Fragment 管理器，管理 Fragment 生命周期 |
| **FragmentTransaction.replace()** | 替换 Fragment（像换卡片） |
| **RadioGroup + RadioButton** | 快速实现底部 Tab 导航 |
| **arguments / Bundle** | Fragment 接收参数的方式 |
| **newInstance() 工厂方法** | 创建 Fragment 并传参的标准写法 |