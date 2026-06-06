好的，开始实现第23天！SeekBar拖动条 + 音量/亮度调节。

先在布局中添加亮度+音量 SeekBar，放在自动登录开关和退出按钮之间：

现在修改 `SettingActivity.kt`，添加 SeekBar 监听和保存逻辑：

✅ **第23天全部完成！** 零 lint 错误，可以直接运行！

---

## 📋 第23天完成清单

### 修改文件（2个）

| 文件 | 改动 |
|---|---|
| `activity_setting.xml` | 在自动登录开关下方新增亮度 SeekBar + 音量 SeekBar，带白色卡片背景 |
| `SettingActivity.kt` | 新增 SeekBar 绑定、SP 读写、拖动监听（实时更新百分比+松开保存） |

---

## 🔑 SeekBar 核心知识

### 1. XML 属性
```xml
android:max="100"      <!-- 最大值100，范围0~100 -->
android:progress="50"  <!-- 当前进度（代码动态设置） -->
```

### 2. 拖动监听三个回调
| 方法 | 触发时机 | 用途 |
|---|---|---|
| `onProgressChanged` | 拖动中，每次进度变化 | **实时更新百分比文字** |
| `onStartTrackingTouch` | 手指按下拖动条 | 一般不处理 |
| `onStopTrackingTouch` | 手指松开拖动条 | **保存数据到 SP** |

### 3. 保存与读取
```kotlin
// 读取：getInt("键名", 默认值)
val brightness = sp.getInt("brightness", 50)

// 保存：putInt("键名", 进度值).apply()
sp.edit().putInt("brightness", seekBar.progress).apply()
```

---

## ✅ 运行效果

1. 设置页新增 **亮度滑块** + **音量滑块**
2. 拖动时**实时显示百分比**（"当前：75%"）
3. 松开后**自动保存** + Toast 提示
4. 退出再进 → **数据不丢失**，恢复上次调节的值