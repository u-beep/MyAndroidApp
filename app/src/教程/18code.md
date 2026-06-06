好的，开始实现第18天！统一样式 + 自定义Dialog + UI美化。

先读取需要修改的现有文件：

还需要读取其他页面布局：

好，所有文件都确认存在。现在开始逐步实现！

### 步骤1：修改 colors.xml 统一颜色管理

### 步骤2：创建 dimens.xml

### 步骤3：创建 drawable/item_bg.xml 圆角背景

### 步骤4：创建 drawable/dialog_bg.xml 弹窗圆角背景

### 步骤5：创建 dialog_delete.xml 自定义删除弹窗布局

### 步骤6：修改 themes.xml 全局主题

### 步骤7：修改 item_user.xml 圆角卡片 + 引用颜色尺寸

### 步骤8：修改 UserAdapter.kt 增加长按回调

现在在 onBindViewHolder 中添加长按事件：

### 步骤9：修改 MainActivity.kt 长按弹出删除弹窗

### 步骤10：替换各页面XML中的硬编码颜色和尺寸

替换 activity_main.xml 中的硬编码：

替换 activity_login.xml 中的硬编码：

替换 activity_memo.xml 中的硬编码：

替换 activity_memo_edit.xml：

替换 item_memo.xml 中的硬编码：

替换 activity_register.xml 和 activity_change_pwd.xml 中的硬编码：

替换 activity_change_pwd.xml：

### 步骤11：检查 lint 错误

全部完成，没有 lint 错误！

---

## ✅ 第18天 统一样式 + 自定义Dialog + UI美化 完成完成！

### 📁 新建文件（4个）

| 文件 | 作用 |
|------|------|
| `dimens.xml` | 统一尺寸管理（间距dp、字号sp） |
| `drawable/item_bg.xml` | 圆角白色卡片背景（12dp圆角） |
| `drawable/dialog_bg.xml` | 弹窗圆角白色背景（16dp圆角） |
| `layout/dialog_delete.xml` | 自定义删除确认弹窗布局 |

### 📝 修改文件（9个）

| 文件 | 改动 |
|------|------|
| `colors.xml` | 添加统一颜色常量（主题色、背景色、文字色等） |
| `themes.xml` | 全局主题：统一页面底色、状态栏色、强调色 |
| `item_user.xml` | 白色圆角卡片样式 + 引用@color/@dimen |
| `item_memo.xml` | 白色圆角卡片样式 + 引用@color/@dimen |
| `activity_main.xml` | 硬编码颜色/尺寸 → @color/@dimen |
| `activity_login.xml` | 硬编码颜色/尺寸 → @color/@dimen |
| `activity_register.xml` | 硬编码颜色/尺寸 → @color/@dimen |
| `activity_change_pwd.xml` | 硬编码颜色/尺寸 → @color/@dimen |
| `activity_memo.xml` | 硬编码颜色/尺寸 → @color/@dimen |
| `activity_memo_edit.xml` | 硬编码颜色/尺寸 → @color/@dimen |
| `UserAdapter.kt` | 新增 `longClick` 长按回调 |
| `MainActivity.kt` | 长按条目弹出自定义删除确认Dialog |

### 🔑 今日核心知识点

1. **资源统一管理**：
    - `colors.xml` → 所有颜色，修改一处全局生效
    - `dimens.xml` → 所有尺寸，不用到处写魔法数字

2. **全局主题 Theme**：
    - `colorPrimary` → 主色调（状态栏、默认按钮）
    - `android:windowBackground` → 页面底色
    - `colorAccent` → 控件强调色

3. **自定义 Dialog**：
   ```
   Dialog → setContentView(自定义布局) → setLayout(宽,高) → setBackgroundDrawableResource(透明) → show()
   ```

4. **长按删除流程**：
   ```
   长按条目 → UserAdapter.longClick回调 → MainActivity弹出自定义Dialog → 确认 → UserDao.deleteUser() → 刷新列表
   ```

### 🚀 运行效果

1. **APP整体风格统一**：蓝色状态栏 + 浅灰页面底色
2. **用户列表白色圆角卡片**，比之前的灰色条目更美观
3. **长按任意用户条目** → 弹出圆角确认弹窗 → 点"确定删除"从数据库删除并刷新列表
4. 以后想改颜色？只改 `colors.xml` 一处即可！