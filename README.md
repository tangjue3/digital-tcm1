# 数字中医

这是从原 `互联网+` 目录整理出的总项目入口。新目录采用“先复制、后整理”的方式生成，旧目录没有被移动或删除，可随时回退。

## 目录结构

```text
数字中医
├─ apps
│  ├─ mobile-app        # uni-app + Vue3 用户端
│  └─ admin-web         # Vue3 + Vite + Element Plus 管理端，当前主用前端
├─ server
│  └─ ruoyi-backend     # 若依后端，ruoyi-admin 为启动入口
├─ database
│  └─ sql               # 数据库初始化脚本副本
├─ scripts              # 新的统一启动、停止、联调脚本
├─ docs                 # 迁移说明、环境说明和整理报告
├─ env                  # 环境变量示例
├─ start-project.bat    # 启动 Redis、后端、管理端
└─ stop-project.bat     # 停止由新脚本打开的窗口
```

## 子项目用途

- `apps/mobile-app`：用户端 app，保留 uni-app 源码、页面、静态资源、配置和锁文件。
- `apps/admin-web`：当前实际使用的管理端前端，保留 `BackendSmokeTest.vue` 和联调请求封装。
- `server/ruoyi-backend`：当前真正后端，保留若依模块、SQL、`webapi` 扩展服务源码和 `ruoyi-ui` 参考前端源码。
- `database/sql`：从后端 `sql` 目录单独复制出来，方便交付和部署时直接查找。

## 快速启动

1. 准备 MySQL，创建或确认数据库 `tcm_vsl`，按需要导入 `database/sql` 下 SQL。
2. 准备 Redis，默认脚本查找 `D:\Redis\redis-server.exe`。如路径不同，先设置环境变量 `REDIS_DIR`。
3. 在根目录运行检查：`start-project.bat check`。
4. 检查路径无误后运行：`start-project.bat`。
5. 需要启动用户端 H5 时，再运行：`start-app.bat`。
6. 需要停止脚本打开的窗口时，运行：`stop-project.bat`。

## 默认端口

- Redis：`127.0.0.1:6379`
- 若依后端：`http://127.0.0.1:8080`
- 管理端前端：`http://127.0.0.1:8089`
- 用户端 H5：`http://localhost:5174`

## 中医AI小助手 RAG

管理端右下角“中医AI小助手”已接入后端 RAG 接口：`POST /system/ai/tcm-chat`。DeepSeek Key 只在后端读取，不写入前端。

知识库下载：https://hf-mirror.com/datasets/michaelwzhu/ShenNong_TCM_Dataset/resolve/main/ChatMed_TCM-v0.2.json

后端环境变量示例：

```text
DEEPSEEK_API_KEY=你的key
DEEPSEEK_BASE_URL=https://api.deepseek.com
DEEPSEEK_MODEL=deepseek-chat
TCM_RAG_TOP_K=5
TCM_RAG_KNOWLEDGE_PATHS=
```

知识库默认自动扫描 `data`、`knowledge`、`kb`、`rag`、`database`、`database/sql`、`server/ruoyi-backend/sql` 等目录，支持 `json/txt/md/csv/sql`。如需指定文件或目录，可用 `TCM_RAG_KNOWLEDGE_PATHS` 填入逗号分隔路径。

调试接口：

- `GET /system/ai/tcm-kb/status`：查看已索引知识库文件。
- `POST /system/ai/tcm-kb/reload`：重新扫描并构建本地索引。

## 验证

- `start-project.bat check` 能正确打印新目录下的 backend/admin/mobile 路径。
- `server/ruoyi-backend` 执行 `mvn -pl ruoyi-admin -am install -DskipTests` 后，`ruoyi-admin` 能启动到 `8080`。
- `apps/admin-web` 能启动到 `8089`，`/api` 代理到 `8080`。
- `scripts/test-backend.bat` 能完成 6 步接口冒烟测试。
- `scripts/start-mobile-h5.bat` 能打开用户端 H5 工程。

