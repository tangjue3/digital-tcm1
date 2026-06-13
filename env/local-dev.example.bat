@echo off
REM Copy this file to local-dev.bat and fill in your own local secrets.

set "HTTP_PROXY="
set "HTTPS_PROXY="
set "ALL_PROXY="
set "GIT_HTTP_PROXY="
set "GIT_HTTPS_PROXY="

set "MYSQL_URL=jdbc:mysql://localhost:3306/tcm_vsl?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%%2B8"
set "MYSQL_USERNAME=root"
set "MYSQL_PASSWORD=123456"

set "TOKEN_SECRET=change-me-for-local-dev"
set "DRUID_LOGIN_USERNAME=ruoyi"
set "DRUID_LOGIN_PASSWORD=123456"

REM DeepSeek + local TCM RAG. Keep the key in backend environment only.
set "DEEPSEEK_API_KEY=your_deepseek_key"
set "DEEPSEEK_BASE_URL=https://api.deepseek.com"
set "DEEPSEEK_MODEL=deepseek-chat"
set "TCM_RAG_TOP_K=5"
set "TCM_RAG_KNOWLEDGE_PATHS="
