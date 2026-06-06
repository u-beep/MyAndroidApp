好的，开始实现第21天！Switch开关 + 设置页面 + 自动登录。

先读取需要修改的文件：

好的，我已经看完了所有需要修改的文件。现在开始逐步实现！先读取现有的颜色和尺寸资源文件，确保新布局使用统一的资源：

现在开始创建文件。首先创建设置页面布局，使用项目中统一的颜色和尺寸资源：

现在创建 `SettingActivity.kt`：

现在在 `activity_main.xml` 中添加设置按钮（放在"打开相册"按钮下方）：

现在修改 `MainActivity.kt`，添加设置按钮跳转：

现在修改 `LoginActivity.kt`，添加自动登录判断逻辑：

现在注册 `SettingActivity` 到 `AndroidManifest.xml`：

现在检查 lint 错误：

没有 lint 错误！最后验证一下所有修改的文件是否正确：

所有文件都正确！让我更新 todo 状态：

✅ **第21天全部完成！** 所有代码已就绪，零错误。下面是改动总结：

---

## 📋 第21天完成清单

### 新建文件（2个）
| 文件 | 说明 |
|---|---|
| `activity_setting.xml` | 设置页面布局（Switch开关 + 退出登录按钮） |
| `SettingActivity.kt` | 设置页面逻辑（自动登录开关 + 退出登录） |

### 修改文件（4个）
| 文件 | 改动 |
|---|---|
| `activity_main.xml` | 新增"设置"按钮（灰色，圆角12dp） |
| `MainActivity.kt` | 新增设置按钮跳转到 `SettingActivity` |
| `LoginActivity.kt` | 新增自动登录判断（onCreate顶部） |
| `AndroidManifest.xml` | 注册 `SettingActivity` |

---

## 🔑 核心逻辑说明

### 1. 自动登录流程
```
打开APP → LoginActivity.onCreate()
  → 读取 SETTING 中的 auto_login 开关
  → 读取 USER_DATA 中的 account
  → 开关打开 && 账号不为空 → 直接跳 MainActivity（传账号）
  → 否则 → 正常显示登录页
```

### 2. 退出登录防坑
退出登录时 **同时关闭自动登录开关**，否则用户退出后下次打开还会自动登录，这是一个常见bug，我们已经处理好了！

### 3. 两个 SP 文件各司其职
- `"USER_DATA"` → 保存账号、密码、记住密码标记
- `"SETTING"` → 保存自动登录开关状态

---

## 🎯 第21天必背3句话
1. **Switch = 开关控件**，`isChecked` 获取/设置开关状态
2. **setOnCheckedChangeListener** = 开关状态改变时自动触发
3. **SP 保存开关状态** → 实现打开APP自动登录