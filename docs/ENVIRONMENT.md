# 环境准备说明

## 基础环境

- JDK：建议 JDK 8 或 JDK 11，需能运行若依 Spring Boot 后端。
- Maven：建议 3.6+，用于编译 `server/ruoyi-backend`。
- Node.js：建议 Node 18+，用于 `apps/admin-web` 和 `apps/mobile-app` 的前端依赖安装与开发启动。
- MySQL：需要数据库 `tcm_vsl`。
- Redis：默认 `127.0.0.1:6379`，脚本默认查找 `D:\Redis\redis-server.exe`。

## MySQL

1. 创建数据库：`tcm_vsl`。
2. 根据实际库状态导入 `database/sql` 中的脚本：
   - `tcm_vsl.sql`：业务库脚本。
   - `ry_20220822.sql`：若依基础表脚本。
   - `quartz.sql`：定时任务表脚本。
3. 检查 `server/ruoyi-backend/ruoyi-admin/src/main/resources/application-druid.yml` 中的数据库账号、密码和库名。

## Redis

- 默认地址：`127.0.0.1:6379`。
- 默认本机路径：`D:\Redis`。
- 如 Redis 安装路径不同，可在运行脚本前设置：

```bat
set REDIS_DIR=D:\your\redis\path
start-project.bat
```

如 Redis 已经作为服务运行，可以忽略脚本中的 Redis 路径警告。

## 端口

- `8080`：若依后端 `ruoyi-admin`。
- `8089`：管理端前端 `apps/admin-web`。
- `6379`：Redis。
- `3306`：MySQL，按本机配置为准。
- 用户端 H5 端口固定为 `5174`。

## 推荐启动顺序

1. MySQL。
2. Redis。
3. 若依后端 `ruoyi-admin`。
4. 管理端前端 `apps/admin-web`。
5. 用户端 H5 `apps/mobile-app`。
6. 需要语音、图像等扩展能力时，再启动 `server/ruoyi-backend/webapi/webapi`。

统一脚本 `start-project.bat` 会按 Redis、后端编译、后端启动、管理端启动的顺序打开窗口。

## 协作同学拉取后的准备

不要提交或复制 `node_modules`、`dist`、`target` 这些本机生成目录。新机器拉取仓库后按下面步骤恢复环境：

```bat
cd apps\admin-web
npm install

cd ..\mobile-app
npm install

cd ..\..\server\ruoyi-backend
mvn -pl ruoyi-admin -am install -DskipTests
```

如果只改前端，通常只需要在对应 `apps/*` 目录执行 `npm install`。如果要跑后端，需要先准备 MySQL、Redis，并导入 `database/sql` 下的 SQL。

## 本地密钥与密码

仓库不会提交真实数据库密码和第三方 API key。需要在本机环境变量或 IDE 运行配置中设置：

- `MYSQL_URL`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `TOKEN_SECRET`
- `BAIDU_API_KEY`
- `BAIDU_SECRET_KEY`
- `BAIDU_SPEECH_APP_ID`
- `BAIDU_SPEECH_API_KEY`
- `BAIDU_SPEECH_SECRET_KEY`
- `XFYUN_LFASR_APP_ID`
- `XFYUN_LFASR_SECRET_KEY`
- `XFYUN_AUDIO_API_KEY`
- `XFYUN_AUDIO_API_SECRET`
- `XFYUN_OST_APP_ID`
- `XFYUN_OST_API_KEY`
- `XFYUN_OST_API_SECRET`
- `XFYUN_TTS_APP_ID`
- `XFYUN_TTS_API_KEY`
- `XFYUN_TTS_API_SECRET`
- `XFYUN_REWRITE_APP_ID`
- `XFYUN_REWRITE_API_KEY`
- `XFYUN_REWRITE_API_SECRET`
- `XFYUN_OCR_APP_ID`
- `XFYUN_OCR_API_KEY`
- `XFYUN_ISE_APP_ID`
- `XFYUN_ISE_API_KEY`
- `XFYUN_ISE_API_SECRET`
- `BAIDU_TONGUE_ACCESS_KEY`
- `BAIDU_TONGUE_SECRET_KEY`

管理端前端如果要使用讯飞星火 WebSocket 能力，在 `apps/admin-web` 下新建 `.env.local`：

```ini
VITE_XFYUN_SPARK_APP_ID=
VITE_XFYUN_SPARK_API_KEY=
VITE_XFYUN_SPARK_API_SECRET=
```

`.env.local` 只放在本机，不要提交到 GitHub。
