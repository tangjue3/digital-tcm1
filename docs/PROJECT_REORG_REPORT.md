# 项目整理报告

## 必须复制

- app 用户端：当前 uni-app + Vue3 主工程。
- 管理端前端：外层 `ISLab`，包含当前联调页面和脚本。
- 若依后端：`tcm-vsl` 根目录，保留若依模块结构。
- SQL 脚本：后端 `sql` 目录，同时放入 `database/sql`。
- Redis/后端/管理端启动脚本：已改为 `scripts/start-all.bat`。
- 联调测试脚本：已改为 `scripts/test-backend.ps1` 与 `scripts/test-backend.bat`。
- `BackendSmokeTest.vue`：保留在 `apps/admin-web/src/views`。

## 可选复制

- `webapi`：已随若依后端保留，作为语音、图像等扩展能力，按需启动。
- `ruoyi-ui`：已随若依后端保留源码，但当前主用管理端是 `apps/admin-web`。
- 原 app 接入分析文档：保留在 app 工程中，作为历史上下文。

## 排除内容

- `node_modules`、`target`、`dist`、`unpackage`。
- `.idea`、`.hbuilderx`、日志文件。
- 内层 `ISLab\ISLab` 和 `ISLab (平台项目源码)new`。
- app 内嵌 `uni-shop-2` 旧模板/副本。

## 新入口

- 总入口：`D:\学校比赛项目\互联网+\数字中医`
- 启动检查：`start-project.bat check`
- 启动主链路：`start-project.bat`
- 后端冒烟测试：`scripts\test-backend.bat`
- 用户端 H5：`start-app.bat`

