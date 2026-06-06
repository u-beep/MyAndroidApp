好的，开始实现第19天！动态权限申请 + 打开本地图片功能。

先读取需要修改的文件：

好，现在开始逐步实现。

### 步骤1：修改 AndroidManifest.xml 添加存储权限

### 步骤2：创建 PermissionUtil.kt 权限工具类

### 步骤3：修改 activity_main.xml 添加打开相册按钮和ImageView

### 步骤4：修改 MainActivity.kt 添加权限申请和相册功能

### 步骤5：检查 lint 错误

全部完成，没有 lint 错误！

---

## ✅ 第19天 动态权限申请 + 打开相册 实现完成！

### 📁 新建文件（1个）

| 文件 | 作用 |
|------|------|
| `PermissionUtil.kt` | 权限工具类（判断权限 + 申请权限，全项目复用） |

### 📝 修改文件（3个）

| 文件 | 改动 |
|------|------|
| `AndroidManifest.xml` | 添加3个存储权限声明 |
| `activity_main.xml` | 新增绿色「打开手机相册图片」按钮 + ImageView图片展示区 |
| `MainActivity.kt` | 完整的权限申请流程 + 打开相册 + 回显图片 |

### 🔑 核心知识点回顾

1. **两种权限**：
   | 类型 | 示例 | 申请方式 |
   |------|------|---------|
   | 普通权限 | 网络、震动 | Manifest声明即可 |
   | 危险权限 | 存储、相机、定位 | 必须动态申请 |

2. **权限申请三步流程**：
   ```
   ① 检查权限：PermissionUtil.hasPermission()
   ② 有权限 → 直接使用功能
   ③ 没权限 → PermissionUtil.requestPerm() → onRequestPermissionsResult() 回调
   ```

3. **Android版本适配**（自动处理）：
   ```
   Android 12及以下 → READ_EXTERNAL_STORAGE + WRITE_EXTERNAL_STORAGE
   Android 13+     → READ_MEDIA_IMAGES（新增专用图片权限）
   ```

4. **打开相册并回显图片**：
   ```
   ACTION_PICK + type="image/*" → startActivityForResult()
   → onActivityResult() → data?.data (URI) → ivShow.setImageURI(uri)
   ```

### 🚀 运行效果

1. 点击「打开手机相册图片」按钮
2. **第一次**：弹出系统权限弹窗 → 点"允许" → 跳转相册选图 → 图片显示在主页
3. **以后**：已授权，直接打开相册，不再弹权限框
4. 如果点了"拒绝" → 提示"需要存储权限才能打开相册"