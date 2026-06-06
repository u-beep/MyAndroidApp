好的，开始实现第17天！SQLite 数据库 + 增删改查，把用户系统从 SP 升级到数据库。

    先读取所有需要修改的文件：

    好，所有文件都读取完毕。现在开始逐步创建和修改！

    ### 步骤1：创建 MySQLiteHelper.kt

    ### 步骤2：创建 UserDao.kt

    ### 步骤3：修改 RegisterActivity.kt → 改用数据库

    现在替换注册保存逻辑（从 SP 改为数据库）：

    ### 步骤4：修改 LoginActivity.kt → 改用数据库

    现在替换登录验证逻辑（从 SP 改为数据库）：

    现在替换"修改密码"按钮中的账号存在性检查：

    ### 步骤5：修改 MainActivity.kt → 从数据库读取列表

    ### 步骤6：修改 ChangePwdActivity.kt → 改用数据库

    ### 步骤7：检查 lint 错误

    全部完成，没有 lint 错误！

    ---

    ## ✅ 第17天 SQLite 数据库升级完成！

    ### 📁 新建文件（2个）

    | 文件 | 作用 |
    |------|------|
    | `MySQLiteHelper.kt` | 数据库帮助类：建库（`user_db.db`）、建表（`user_table`） |
    | `UserDao.kt` | 数据操作类：6个方法封装增删改查 |

    ### 📝 修改文件（4个）

    | 文件 | 改动 |
    |------|------|
    | `RegisterActivity.kt` | `SP.contains()` + `SP.putString()` → `userDao.addUser()` |
    | `LoginActivity.kt` | `SP.getString()` + `split("\\|")` → `userDao.login()` |
    | `MainActivity.kt` | `SP.all.keys` 遍历切割 → `userDao.getAllUser()` + `userDao.getUserByAccount()` |
    | `ChangePwdActivity.kt` | `SP.getString()` + 手动拼接覆盖 → `userDao.login()` + `userDao.updatePwd()` |

    ### 🔑 数据库 vs SP 对比（升级前→升级后）

    | 操作 | 以前（SP） | 现在（数据库） |
    |------|-----------|---------------|
    | **注册** | `sp.contains()` 判重 + `putString("密码\|性别\|爱好")` | `userDao.addUser()` 一行搞定 |
    | **登录** | `getString()` + `split("\\|")` 切割取密码 | `userDao.login()` 直接返回密码 |
    | **查用户信息** | `getString()` + 手动切割3段 | `userDao.getUserByAccount()` 返回User对象 |
    | **改密码** | 切割 → 拼接 `"新密码\|性别\|爱好"` → `putString()` 覆盖 | `userDao.updatePwd()` 只改密码列 |
    | **查所有用户** | `sp.all.keys` 遍历 + 逐条切割 | `userDao.getAllUser()` 直接返回List |

    ### 🏗️ 数据库表结构 `user_table`

    | 列名 | 类型 | 说明 |
    |------|------|------|
    | `id` | INTEGER | 主键，自增 |
    | `account` | TEXT UNIQUE | 账号（唯一，自动防重复） |
    | `pwd` | TEXT | 密码 |
    | `sex` | TEXT | 性别 |
    | `hobby` | TEXT | 爱好 |

    ### ⚠️ 重要提示

    由于存储方式从 SP 改为数据库，**之前用 SP 注册的账号数据不会自动迁移到数据库**。你需要：
    - **重新注册新账号**来测试数据库功能
    - 或者卸载APP重装（会清空所有SP数据，全新开始）