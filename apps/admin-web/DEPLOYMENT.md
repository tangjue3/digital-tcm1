# 部署文档

## 项目概述

这是一个基于 Vue 3 + Vite + Element Plus 的前端项目，用于中医智能问诊系统。项目包含患者列表、聊天功能和患者详细信息展示等功能。

## 前端部署步骤

### 1. 环境准备

#### 1.1 安装 Node.js

- 访问 [Node.js 官网](https://nodejs.org/zh-cn/) 下载并安装 Node.js（推荐版本 16.0 或以上）
- 验证安装：
  ```bash
  node -v
  npm -v
  ```

#### 1.2 克隆项目

- 克隆项目到本地：
  ```bash
  git clone <项目地址>
  cd ISLab1.1
  ```

### 2. 安装依赖

- 安装项目依赖：
  ```bash
  npm install
  ```

### 3. 配置环境

#### 3.1 配置 API 代理（可选）

- 编辑 `vite.config.js` 文件，修改代理配置：
  ```javascript
  server: {
    port: 8088,
    proxy: {
      '/api': {
        target: 'http://your-server-host:8202/prod-api', // 目标服务器地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    }
  }
  ```

### 4. 构建项目

- 执行构建命令：
  ```bash
  npm run build
  ```
- 构建产物将生成在 `dist` 目录中

### 5. 部署到服务器

#### 5.1 本地预览（可选）

- 执行预览命令：
  ```bash
  npm run preview
  ```
- 访问 `http://localhost:4173` 查看预览效果

#### 5.2 部署到 Nginx

1. 安装 Nginx（以 Ubuntu 为例）：
   ```bash
   sudo apt update
   sudo apt install nginx
   ```

2. 启动 Nginx：
   ```bash
   sudo systemctl start nginx
   ```

3. 配置 Nginx 虚拟主机：
   - 创建配置文件：
     ```bash
     sudo nano /etc/nginx/sites-available/islab
     ```
   - 配置内容：
     ```nginx
     server {
         listen 80;
         server_name your-domain.com;

         root /path/to/ISLab1.1/dist;
         index index.html;

         location / {
             try_files $uri $uri/ /index.html;
         }

         # 如果需要代理 API 请求
         location /api {
             proxy_pass http://your-server-host:8202/prod-api;
             proxy_set_header Host $host;
             proxy_set_header X-Real-IP $remote_addr;
             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
             proxy_set_header X-Forwarded-Proto $scheme;
         }
     }
     ```

4. 启用配置：
   ```bash
   sudo ln -s /etc/nginx/sites-available/islab /etc/nginx/sites-enabled/
   ```

5. 测试配置：
   ```bash
   sudo nginx -t
   ```

6. 重启 Nginx：
   ```bash
   sudo systemctl restart nginx
   ```

#### 5.3 部署到其他静态服务器

- 将 `dist` 目录下的所有文件复制到服务器的静态文件目录中
- 确保服务器配置正确处理 Vue 单页应用的路由（需要将所有请求重定向到 index.html）

## 后端部署步骤

### 1. 环境准备

#### 1.1 安装 Java 环境（假设后端使用 Java）

- 安装 JDK 11 或以上版本：
  ```bash
  sudo apt update
  sudo apt install openjdk-11-jdk
  ```
- 验证安装：
  ```bash
  java -version
  ```

#### 1.2 安装数据库（假设使用 MySQL）

- 安装 MySQL：
  ```bash
  sudo apt update
  sudo apt install mysql-server
  ```
- 启动 MySQL：
  ```bash
  sudo systemctl start mysql
  ```
- 配置 MySQL：
  ```bash
  sudo mysql_secure_installation
  ```

### 2. 部署后端服务

#### 2.1 上传后端代码

- 将后端代码上传到服务器
- 编译打包：
  ```bash
  mvn clean package
  ```

#### 2.2 配置数据库

- 创建数据库：
  ```sql
  CREATE DATABASE islab;
  ```
- 导入数据库表结构和初始数据

#### 2.3 配置应用

- 编辑配置文件，设置数据库连接信息、端口等

#### 2.4 启动服务

- 使用 Spring Boot 内置 Tomcat 启动：
  ```bash
  java -jar islab-backend.jar
  ```

- 或使用系统服务管理：
  - 创建服务文件：
    ```bash
    sudo nano /etc/systemd/system/islab-backend.service
    ```
  - 配置内容：
    ```ini
    [Unit]
    Description=ISLab Backend Service
    After=network.target

    [Service]
    User=ubuntu
    ExecStart=/usr/bin/java -jar /path/to/islab-backend.jar
    WorkingDirectory=/path/to/backend
    Restart=always

    [Install]
    WantedBy=multi-user.target
    ```
  - 启动服务：
    ```bash
    sudo systemctl daemon-reload
    sudo systemctl start islab-backend
    sudo systemctl enable islab-backend
    ```

### 3. 验证后端服务

- 访问 API 接口：
  ```bash
  curl http://localhost:8202/prod-api/health
  ```

## 部署架构

```
+-------------------+     +-------------------+     +-------------------+
|   前端服务器      |     |   后端服务器      |     |   数据库服务器    |
|   (Nginx)         |     |   (Spring Boot)   |     |   (MySQL)         |
+-------------------+     +-------------------+     +-------------------+
|                   |     |                   |     |                   |
|  /dist 静态文件    | <-> |  /prod-api 接口   | <-> |  数据存储         |
|                   |     |                   |     |                   |
+-------------------+     +-------------------+     +-------------------+
```

## 常见问题排查

### 前端问题

1. **页面空白**
   - 检查 Nginx 配置是否正确
   - 检查 dist 目录是否正确部署
   - 检查浏览器控制台是否有错误

2. **API 调用失败**
   - 检查 API 代理配置是否正确
   - 检查后端服务是否正常运行
   - 检查网络连接是否正常

### 后端问题

1. **服务启动失败**
   - 检查端口是否被占用
   - 检查数据库连接是否正确
   - 查看应用日志

2. **API 接口返回错误**
   - 查看后端日志
   - 检查数据库是否正常
   - 检查业务逻辑是否正确

## 维护建议

1. **定期备份**
   - 定期备份前端代码和配置
   - 定期备份后端代码和数据库

2. **监控服务**
   - 监控前端服务器状态
   - 监控后端服务状态
   - 监控数据库状态

3. **更新依赖**
   - 定期更新前端依赖
   - 定期更新后端依赖
   - 确保依赖安全

4. **性能优化**
   - 前端资源压缩
   - 后端代码优化
   - 数据库索引优化
