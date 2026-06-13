# 晨夕四诊辩养录（数字中医智能诊疗平台）

> 一套面向中医教学与轻诊疗场景的智能问诊 / 报告 / 健康管理平台。  
> 用户通过手机端完成“望闻问切”四诊资料采集与标准化问诊报告提交，医生在 Web 管理端进行审核、医患沟通与中医 AI 智能体辅助决策。

应用名：**爱上中医**（App 名） / **云枢四诊**（主标题） / **云枢四诊管理端**（Web 端）

---

## 一、项目背景与目标

传统中医诊疗高度依赖医师经验，问诊资料（主诉、病史、舌面脉像）多以纸质或口述方式记录，难以结构化、量化、复用。本项目尝试用 Web + 移动端 + 后端 + 中医 AI 的方式：

- **标准化** 用户的四诊资料（基本信息 + 病种/症状 + 主诉 + 影像/语音附件）
- **数字化** 报告流转：App 端采集 → 后端入库 → 管理端审阅 → 医生回复
- **辅助化** 借助 DeepSeek + 本地知识库 RAG，为医生提供体质辨识、医案分析、医患沟通建议
- **教学化** 为中医临床教学场景提供"四诊综合分析报告单"和"诊疗案例分析"示例数据与工具链

> ⚠️ **医疗免责声明**：本平台仅作为中医健康咨询与教学辅助工具，所有 AI 输出均**不能替代医生诊断**，不会给出处方、剂量或自行停换药建议。若出现急症请立即就医。

---

## 二、核心功能

### 📱 用户端 App（uni-app + Vue3）

四大 tab 入口 + 一系列功能页：

| 模块 | 入口 | 关键能力 |
|---|---|---|
| **健康计划** | `pages/health-plan/health-plan.vue` | 7 天处方任务、问诊报告入口、连续执行记录 |
| **健康自测** | `pages/zice/zice.vue` | 四诊（声纹/面/脉/舌）快捷入口 + 体质报告总览 + 脉搏历史趋势 |
| **健康博览** | `pages/expo/expo.vue` | 5 类知识库（好文/食谱/常见病/药材/视频）检索与详情 |
| **个人中心** | `pages/my/my.vue` | 用户资料、退出登录、意见反馈 |

特色功能：
- **问诊报告**（4 步表单：基础信息 → 病种/症状 → 主诉 → 预览提交）— 复诊可沿用上次病情
- **AI 智能问答**（DeepSeek + TCM 知识库 RAG）— 7 类快捷提问
- **医患沟通**（医患 1v1 文本对话，4 秒轮询）
- **声纹自测 / 脉诊测试 / 面诊舌诊采集**（通过文件上传或后端接口完成，结果写入用户档案）
- **完整用户档案**：`/app/user/profile` 接口存储体重、身高、脉率、病史、舌面照片、语音

### 🖥 管理端 Web（Vue3 + Element Plus + ECharts）

| 页面 | 路径 | 作用 |
|---|---|---|
| 首页 Dashboard | `views/HomeNew.vue` | 关键指标卡、问诊趋势、病种分布、紧急关注 |
| 四诊综合分析报告单 | `views/cgList.vue` | 报告列表 / 详情 / 状态流转 |
| 医生诊疗案例分析 | `views/MedicalCaseAnalysis.vue` | 案例分析 |
| 中医诊疗智能体 | `views/AITCMIntelligentAgent.vue` | DeepSeek + 本地 RAG 问答 |
| 中医药知识库 | `views/AITCMKnowledgeBase.vue` | 知识库文件浏览、重新索引 |
| 医患沟通 | `views/MedicalChat.vue` | 医生视角的会话列表 + 与患者对话 |
| 用户管理 | `views/AppUserManagement.vue` | App 用户档案与媒体资料 |

侧栏顶部带全局通知徽章（高风险报告数实时刷新）。

### ⚙️ 后端（若依 RuoYi 3.8.3 + Spring Boot）

业务模块（包路径 `com.ruoyi.system` / `com.ruoyi.web`）：

- `tcm_consult_report` — 问诊报告主表 + 病种子表 + 附件子表（CRUD、Dashboard、紧急关注）
- `tcm_app_user_profile` — App 用户档案（舌面照片、语音、身高体重、脉率、病史）
- `tcm_pulse_record` — 脉搏记录
- `tcm_medical_chat` — 医患聊天（会话与消息）
- `tcm_ai_chat` — App 端 DeepSeek 中转（密钥只读自环境变量/私有配置）
- `tcm_article` / `tcm_video` / `chinesemedicine` / `zhonogyaoshipu` / `commondiseases` — 知识库内容
- `tcm_case` / `tcm_case_records` / `tcm_question` / `tcm_question_detail` — 教学题库与案例
- `tcm_shop` / `tcm_acupoints` — 商城与穴位
- `system/ai/tcm-chat` & `system/ai/tcm-kb/*` — 管理端 AI 智能体（兼容 `POST /system/ai/tcm-chat`，`/tcm-kb/status`，`/tcm-kb/reload`）

---

## 三、目录结构

```
digital-tcm1/
├─ apps/
│  ├─ mobile-app/        # uni-app + Vue3 用户端（端口 5174）
│  │  ├─ pages/          # 页面（4 个 tabbar + 业务页 + 登录注册）
│  │  ├─ components/     # 通用组件
│  │  │  ├─ premium/     # premium 风格 UI 组件库
│  │  │  └─ consult/     # 问诊报告相关组件
│  │  ├─ common/         # API、auth、recorder、mock
│  │  ├─ utils/          # 工具函数（病种搜索、风险规则、本地存储）
│  │  ├─ data/           # 病种树、风险规则等 JSON
│  │  └─ static/         # 静态资源
│  └─ admin-web/         # Vue3 + Vite + Element Plus 管理端（端口 8089）
│     ├─ src/views/      # 页面
│     ├─ src/components/ # 通用组件 + FloatingBall（AI 助手入口）
│     ├─ src/api/        # 业务接口
│     ├─ src/router/     # 路由 + 登录守卫
│     ├─ src/utils/      # axios 封装、auth、mock、tcmAiSession
│     └─ public/         # 静态资源
├─ server/
│  └─ ruoyi-backend/     # 若依 RuoYi 3.8.3 后端（端口 8080）
│     ├─ ruoyi-admin/    # 启动入口，含 controller/system, controller/app, controller/device
│     ├─ ruoyi-system/   # 业务模块（domain、service、mapper、VO/AI/chat）
│     ├─ ruoyi-framework/# 框架配置（安全、Redis、Web、Swagger）
│     ├─ ruoyi-common/   # 通用工具（PageHelper、AjaxResult、AES）
│     ├─ ruoyi-quartz/   # 定时任务
│     ├─ ruoyi-generator/# 代码生成
│     ├─ ruoyi-ui/       # 若依原 Vue2 前端（参考保留，**不作为主用**）
│     ├─ webapi/         # 扩展服务（语音、图像处理）
│     ├─ sql/            # 后端模块内置 SQL（额外副本在 database/sql）
│     └─ config/         # 私有配置目录（application-secret.yml，git 已忽略）
├─ database/
│  └─ sql/               # 初始化 SQL（若依基础 + TCM 业务表）
│     ├─ ry_20220822.sql
│     ├─ tcm_vsl.sql
│     ├─ tcm_consult_report.sql
│     ├─ tcm_consult_report_disease.sql
│     ├─ tcm_consult_report_attachment.sql
│     ├─ tcm_app_user_profile.sql
│     ├─ tcm_pulse_record.sql
│     ├─ dev_app_user.sql        # 开发用测试账号
│     └─ quartz.sql
├─ scripts/              # 启动、停止、测试、数据库工具
│  ├─ start-all.bat      # 一键启动 Redis + 后端 + 管理端
│  ├─ start-app.bat      # 启动 H5 用户端
│  ├─ stop-all.bat
│  ├─ test-backend.bat   # 后端 6 步冒烟测试
│  ├─ test-pulse.bat
│  └─ update-database.bat
├─ docs/                 # 项目说明文档、API 文档、答辩材料
│  ├─ ENVIRONMENT.md     # 环境配置说明
│  ├─ MIGRATION.md       # 目录迁移说明
│  ├─ PROJECT_REORG_REPORT.md
│  ├─ api/consult-report-api.md
│  └─ 答辩问题集.md
├─ logs/                 # 运行日志（git 忽略）
├─ .env.example          # 后端环境变量模板
├─ .gitignore
├─ start-project.bat     # 根目录一键启动（推荐）
├─ stop-project.bat
├─ start-app.bat
└─ README.md
```

---

## 四、技术栈

### 用户端
- uni-app 3.x（Vue 3，H5 / App / 小程序同构）
- Vite 5
- ECharts（健康自测页脉搏趋势）
- 自研 premium 风格组件库（`components/premium/`）
- Web Audio API（H5 端录音）/ uni.getRecorderManager（App 端）

### 管理端
- Vue 3.5 + Composition API + `<script setup>`
- Vite 6
- Element Plus 2.9
- vue-router 4 + pinia
- axios（带拦截器与 mock 降级）
- ECharts 5

### 后端
- Spring Boot（若依 RuoYi 3.8.3）
- MyBatis + MyBatis-Plus 风格的 Mapper XML
- PageHelper 分页
- Druid 数据源 + 监控
- Redis（Token 缓存、分布式会话）
- Spring Security + 自定义 `@PreAuthorize` 权限注解
- 集成 DeepSeek Chat Completions（通过 `RestTemplate` 直连，密钥后端独享）
- 本地 TCM RAG（基于 `TCM_RAG_KNOWLEDGE_PATHS` 扫描 `data/knowledge/kb/rag/database/sql` 目录，支持 `json/txt/md/csv/sql`）

### 数据库
- MySQL 8.x（数据库名 `tcm_vsl`）

---

## 五、快速启动

### 0. 准备环境
- Node.js 18+（Vite 6 需要）
- JDK 1.8 / Maven 3.6+
- MySQL 8.x（默认账号 `root` / `123456`）
- Redis（默认 `127.0.0.1:6379`，默认脚本查找 `D:\Redis\redis-server.exe`，可通过环境变量 `REDIS_DIR` 改）

### 1. 初始化数据库
```bash
# 1) 创建数据库
mysql -u root -p -e "CREATE DATABASE tcm_vsl DEFAULT CHARACTER SET utf8mb4;"

# 2) 按顺序执行 database/sql 下的脚本
mysql -u root -p tcm_vsl < database/sql/ry_20220822.sql         # 若依基础表
mysql -u root -p tcm_vsl < database/sql/tcm_vsl.sql            # 业务数据
mysql -u root -p tcm_vsl < database/sql/tcm_app_user_profile.sql
mysql -u root -p tcm_vsl < database/sql/tcm_consult_report.sql
mysql -u root -p tcm_vsl < database/sql/tcm_consult_report_disease.sql
mysql -u root -p tcm_vsl < database/sql/tcm_consult_report_attachment.sql
mysql -u root -p tcm_vsl < database/sql/tcm_pulse_record.sql
mysql -u root -p tcm_vsl < database/sql/dev_app_user.sql       # 可选：开发测试账号
mysql -u root -p tcm_vsl < database/sql/quartz.sql
```

### 2. 配置私有密钥
后端私有配置模板已存在（`server/ruoyi-backend/config/application-secret.example.yml`），首次使用：

```bat
copy server\ruoyi-backend\config\application-secret.example.yml server\ruoyi-backend\config\application-secret.yml
```

然后编辑该文件，填入真实 `deepseek.api-key` 等。  
该文件**已加入 .gitignore**，不会被提交。

或使用环境变量方式（推荐 Linux/CI）：
```bash
export DEEPSEEK_API_KEY=sk-xxxxxxxx
export MYSQL_PASSWORD=xxxxxx
export REDIS_DIR=/usr/local/redis
```

### 3. 一键启动（Windows，推荐）
```bat
:: 在项目根目录
start-project.bat check        :: 校验路径
start-project.bat               :: 启动 Redis + 后端 + 管理端
start-app.bat                   :: 启动用户端 H5（如需要）
stop-project.bat                :: 停止
```

### 4. 手动启动

**后端**
```bash
cd server/ruoyi-backend
mvn -pl ruoyi-admin -am install -DskipTests
cd ruoyi-admin
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.additional-location=optional:file:../config/application-secret.yml
```

**管理端**
```bash
cd apps/admin-web
npm install
npm run dev      # http://localhost:8089
```

**用户端 H5**
```bash
cd apps/mobile-app
npm install
npm run dev:h5   # http://localhost:5174（/api 代理到 8080）
```

### 5. 默认端口与账号
| 服务 | 地址 | 账号 |
|---|---|---|
| Redis | `127.0.0.1:6379` | - |
| 后端 Spring Boot | `http://127.0.0.1:8080` | - |
| 管理端 Web | `http://127.0.0.1:8089` | `admin` / `admin123` |
| 用户端 H5 | `http://localhost:5174` | 手机号 `13800138000` / `123456` |

---

## 六、关键约定

### 风险评级
病种分 4 级：`normal` / `medium` / `high` / `critical`。
- 数据源：[`apps/mobile-app/data/tcmDiseaseTree.json`](apps/mobile-app/data/tcmDiseaseTree.json)
- 升级规则：[`apps/mobile-app/data/tcmRiskRules.json`](apps/mobile-app/data/tcmRiskRules.json) + [`apps/mobile-app/utils/riskRules.js`](apps/mobile-app/utils/riskRules.js)
- 后端在 `TcmConsultReportServiceImpl` 解析 selectedDiseases 后写入 `tcm_consult_report.highest_risk_level`
- 前端管理端 Dashboard 与报告详情共用此字段做高风险筛选

### 报告四步表单
1. 基础信息：姓名 / 性别 / 年龄 / 基础病 / 过敏 / 既往 / 用药 / 就诊类型
2. 病种/症状选择：树形分类 + 搜索 + 沿用上次
3. 补充说明：主诉 / 出现时间 / 严重程度（0–10）/ 突然加重 / 影响生活
4. 预览并提交：附件上传、报告编号、风险等级、提交状态

提交到后端 `POST /app/consult/report`，后端事务化插入主表 + 病种子表 + 附件子表。

### 接口代理
- 用户端 [vite.config.js](apps/mobile-app/vite.config.js) 把 `/api` 代理到 `8080`（H5 开发）
- 管理端 [vite.config.js](apps/admin-web/vite.config.js) 同上
- App 端（真机/模拟器）走 `127.0.0.1:8080` 直连

### Mock 降级
- 用户端 [`common/mock.js`](apps/mobile-app/common/mock.js) 默认 `USE_MOCK = true`，外部 AI 接口（faceAi / iseC）失败时自动回落演示数据
- 管理端 [`src/utils/mock.js`](apps/admin-web/src/utils/mock.js) 通过环境变量 `VITE_USE_MOCK=true` 开启

### AI 智能问答
- App 端 → `POST /tcm/ai/chat` → [`TcmAiChatServiceImpl`](server/ruoyi-backend/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/TcmAiChatServiceImpl.java) → DeepSeek Chat Completions
- 管理端 → `POST /system/ai/tcm-chat`（兼容入口）→ 同后端服务
- 本地 RAG 知识库：默认扫描 `data/knowledge/kb/rag/database/sql`，可通过 `TCM_RAG_KNOWLEDGE_PATHS` 指定额外路径
- 调试接口：`GET /system/ai/tcm-kb/status`、`POST /system/ai/tcm-kb/reload`

---

## 七、安全与隐私

**已落实的隔离措施**：
- 真实 API key、阿里云 appcode 等敏感信息**已全部使用占位符**（`your_deepseek_api_key_here`、`your_aliyun_appcode_here`、`your-server-host`）替代，**不直接出现在任何可被跟踪的文件中**
- `.gitignore` 已排除：
  - `application-secret.yml`（真实密钥文件）
  - `.env`、`.env.*`（除 `.env.example`）
  - `env/local-dev.bat`（个人本地开发脚本）
  - `*.zip`、`*.tar*`（打包文件）
  - `node_modules/`、`dist/`、`target/`、`logs/`
  - `scripts/.runtime/`（运行时状态）

**首次使用**：
1. 拷贝 `application-secret.example.yml` → `application-secret.yml`，填入真实密钥
2. 在代码中搜索 `your-server-host`，替换为你自己的部署地址
3. 同步修改 `.env.example` 里的占位符（保留示例值，避免误传真实 key）

**历史已公开 key 的处理建议**：
如果你之前在公开仓库中暴露过 DeepSeek 等付费 API key，**强烈建议在 DeepSeek 后台撤销旧 key 并重新生成**，再填入新的 `application-secret.yml`。因为 git 历史中可能仍然保留着。

---

## 八、相关文档

- [`docs/ENVIRONMENT.md`](docs/ENVIRONMENT.md) — 环境变量与本地开发指引
- [`docs/MIGRATION.md`](docs/MIGRATION.md) — 目录迁移历史
- [`docs/api/consult-report-api.md`](docs/api/consult-report-api.md) — 问诊报告接口契约
- [`docs/四诊综合分析报告单假数据规范.md`](docs/四诊综合分析报告单假数据规范.md) — 演示数据规范
- [`docs/答辩问题集.md`](docs/答辩问题集.md) — 答辩 Q&A
- [`server/ruoyi-backend/运行说明.md`](server/ruoyi-backend/运行说明.md) — 后端启动细节

---

## 九、致谢

- 基础框架：[RuoYi-Vue](http://www.ruoyi.vip/) 3.8.3
- AI 模型：DeepSeek Chat Completions API
- 中医知识库：参考 Shennong TCM Dataset 等开源资料
- UI 设计：自研 premium 风格，灵感来源于中医传统色调

---

## 十、License

仅供学习与教学使用。涉及真实临床应用需获得当地医疗合规许可。
