# MemoApp 备忘录应用

## 项目简介

MemoApp 是一个基于 Android 的简洁备忘录应用，支持多分类、图片插入、搜索、排序、导入导出等功能，界面采用 Material Design 风格，体验流畅，代码结构清晰，易于扩展和维护。
## 注意事项
登录用户名:admin
登录密码：123456

---

## 项目结构

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/memoapp/
│   │   │   ├── MainActivity.kt           # 登录页
│   │   │   ├── MemoListActivity.kt       # 备忘录列表页
│   │   │   ├── MemoDetailActivity.kt     # 备忘录详情/编辑页
│   │   │   ├── MemoAdapter.kt            # RecyclerView 适配器
│   │   │   ├── Memo.kt                   # 数据实体
│   │   │   ├── AppDatabase.kt            # Room 数据库
│   │   │   └── MemoDao.kt                # 数据库操作接口
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── activity_memo_list.xml
│   │   │   │   ├── activity_memo_detail.xml
│   │   │   │   └── item_memo.xml
│   │   │   ├── drawable/
│   │   │   │   ├── bg_round_rect.xml
│   │   │   │   └── bg_image_border.xml
│   │   │   └── values/
│   │   │       ├── colors.xml
│   │   │       └── themes.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
└── README.md
```

---

## 主要功能亮点

- **多分类管理**：支持自定义分类，分类筛选与切换。
- **图片插入与显示**：每条备忘录可插入本地图片，采用 Glide 实现高效加载与圆角美化。
- **实时搜索与排序**：支持标题/内容关键字搜索，支持按时间升降序排序。
- **数据导入导出**：支持 JSON 格式的备忘录数据导入导出，用户可自定义文件保存/读取路径。
- **Material Design 风格**：整体 UI 采用圆角卡片、主色调、阴影、留白等设计，体验美观。
- **Room+LiveData**：数据层采用 Room 持久化，LiveData 自动刷新 UI，保证数据一致性。
- **协程异步操作**：数据库和文件操作均采用 Kotlin 协程，不卡 UI。

---

## 关键实现细节

### 1. 数据库设计

- 使用 Room 持久化，Memo 实体包含 id、title、content、category、timestamp、imageUri 字段。
- 单例数据库，保证 LiveData 监听数据变更实时刷新。

### 2. 列表与详情页

- 列表页采用 RecyclerView+CardView，MemoAdapter 支持图片圆角显示。
- 详情页支持分类、标题、内容、图片选择与预览，保存/删除操作均为异步。

### 3. 图片处理

- 选择图片采用 Storage Access Framework，存储图片 Uri，Glide 加载并圆角显示。
- 图片边框采用自定义 drawable，提升视觉层次。

### 4. 导入导出

- 导出：ACTION_CREATE_DOCUMENT 让用户自选保存路径，写入 JSON。
- 导入：ACTION_OPEN_DOCUMENT 让用户自选文件，读取 JSON 并批量插入数据库。

### 5. UI 美化

- 统一主色调（purple_500）、圆角背景（bg_round_rect）、卡片阴影。
- 输入框、按钮、搜索框均有圆角和留白，整体风格简洁现代。
- 列表底部按钮与添加按钮错开，防止遮挡。

---

## 运行环境

- Android 7.0 (API 24) 及以上
- 依赖库：Room、Glide、Gson、Material Components

---

## 体验与扩展

- 支持多端数据迁移（导入导出）
- 代码结构清晰，便于二次开发（如云同步、提醒、加密等）

---
