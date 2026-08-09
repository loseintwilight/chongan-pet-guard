# Pet 宠物平台项目

> 一个基于 RuoYi 框架的宠物综合服务平台，包含移动端（uni-app）和后台管理系统（Spring Boot + Vue）。

---

## 项目简介

本项目是一个面向宠物主人的综合服务平台，主要功能包括：

-  **宠物商城** — 商品浏览、购物车、订单、支付、评价、收藏
-  **宠物信息** — 宠物档案、领养、寻宠、救助
-  **活动公告** — 活动发布、报名、通知
-  **店铺管理** — 商家入驻、商品管理、订单处理
-  **用户中心** — 登录注册、收货地址、协议、消息通知
-  **权限管理** — 基于 RuoYi 的 RBAC 角色权限体系

---

## 项目结构

```
Pet/
├── pet_system_app/        # 移动端（uni-app）
├── pet_system_pc/         # PC 后台 + 后端服务（Spring Boot + Vue）
├── add_mall_table.sql     # 商城模块增量脚本
├── pet_vue.sql            # 基础数据库脚本
├── 数据库逻辑.md            # 数据库设计文档
└── README.md              # 本文档
```

---

## 技术栈

### 移动端 `pet_system_app`
- **框架**：[uni-app](https://uniapp.dcloud.net.cn/)（一套代码，多端运行：H5 / 小程序 / Android / iOS）
- **UI 库**：[uni-ui](https://github.com/dcloudio/uni-ui)
- **请求库**：`uni.request` 封装
- **状态管理**：Vuex
- **基础模板**：RuoYi App v1.2.0

### 后台管理 `pet_system_pc/ruoyi-ui`
- **框架**：[Vue 2](https://v2.vuejs.org/) + [Element UI](https://element.eleme.io/)
- **构建工具**：Vue CLI
- **基础模板**：RuoYi Vue

### 后端服务 `pet_system_pc/ruoyi-admin` 等
- **语言**：Java 8
- **框架**：[Spring Boot](https://spring.io/projects/spring-boot) + Spring Security + MyBatis
- **权限**：[RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 基础框架
- **数据库**：MySQL 8.0
- **定时任务**：Quartz

---

## 环境要求

| 工具       | 版本要求         | 说明                       |
| :--------- | :--------------- | :------------------------- |
| Node.js    | ≥ 14.x           | 移动端与前端构建           |
| JDK        | 1.8              | 后端服务                   |
| Maven      | ≥ 3.6            | 后端构建                   |
| MySQL      | 8.0.x            | 数据库                     |
| HBuilder X | 最新版           | 移动端开发与运行（推荐）   |
| Redis      | ≥ 5.x（可选）    | 后端缓存（按需启用）       |

---

## 快速开始

### 1️初始化数据库

数据库脚本位于项目根目录，**必须按顺序执行**：

```bash
# 1. 登录 MySQL
mysql -u root -p

# 2. 先执行基础脚本（创建数据库 pet_vue 及全部基础表）
source pet_vue.sql;

# 3. 再执行商城增量脚本（新增商城表 + 测试数据 + 字段补充）
source add_mall_table.sql;
```

> 详细表结构与业务流程见 [数据库逻辑.md](数据库逻辑.md)。

### 2️ 启动后端服务 `pet_system_pc`

后端服务在 `pet_system_pc/ruoyi-admin/`，使用 Spring Boot。

```bash
# 进入后端模块
cd pet_system_pc/ruoyi-admin

# 修改数据库连接（src/main/resources/application-druid.yml）
#   url: jdbc:mysql://localhost:3306/pet_vue?useUnicode=true&characterEncoding=utf8
#   username: root
#   password: 你的密码

# 启动服务
mvn spring-boot:run
# 或者先打包再运行
mvn clean package -DskipTests
java -jar target/ruoyi-admin.jar
```

后端默认端口：**8080**

### 3️ 启动 PC 后台管理 `pet_system_pc/ruoyi-ui`

```bash
cd pet_system_pc/ruoyi-ui

# 安装依赖（推荐使用国内镜像）
npm install --registry=https://registry.npmmirror.com

# 启动开发服务
npm run dev
```

浏览器访问：<http://localhost:80>

默认账号：
- 管理员：`admin / admin123`

### 4 启动移动端 `pet_system_app`

#### 方式一：HBuilder X（推荐）
1. 下载并打开 [HBuilder X](https://www.dcloud.io/hbuilderx.html)
2. 选择 `文件 → 导入 → 从本地目录导入`，选择 `pet_system_app` 文件夹
3. 修改 `config.js` 中的 `baseUrl` 为你的后端地址：
   ```js
   baseUrl: 'http://127.0.0.1:8080'
   ```
4. 点击 `运行 → 运行到浏览器/小程序/手机` 即可

#### 方式二：CLI
```bash
cd pet_system_app

# H5
npm run dev:h5

# 微信小程序
npm run dev:mp-weixin
```

> 移动端默认入口：`pages/login`（登录页）→ 登录成功后进入 `pages/index`（首页）。

---

## 关键配置说明

### 移动端后端地址

文件：`pet_system_app/config.js`

```js
module.exports = {
  baseUrl: 'http://localhost:8080',   // 后端服务地址
  downloadUrl: 'http://localhost:8080/common/download?name=',
  // ...应用信息
}
```

**注意** **微信小程序调试时**：baseUrl 不能使用 `localhost`，必须使用局域网 IP（如 `http://192.168.1.100:8080`），且后端需要在小程序后台配置为「request 合法域名」。

### 后端跨域与端口

`pet_system_pc/ruoyi-admin/src/main/resources/application.yml`

```yaml
server:
  port: 8080
```

---

## 主要功能模块

### 移动端 `pet_system_app`
| 路径                    | 功能说明           |
| :---------------------- | :----------------- |
| `pages/index`           | 首页               |
| `pages/login`           | 登录（含图形验证码）|
| `pages/register`        | 注册               |
| `pages/mall/*`          | 商城（列表/详情/购物车/订单） |
| `pages/shop/*`          | 商家入驻与店铺     |
| `pages/activity/*`      | 活动               |
| `pages/adoption/*`      | 领养               |
| `pages/lost-pet/*`      | 寻宠               |
| `pages/rescue/*`        | 救助               |
| `pages/notice/*`        | 公告               |
| `pages/user/*`          | 个人中心           |

### 后台 `pet_system_pc/ruoyi-ui`
- 系统管理（用户、角色、菜单、部门、字典）
- 系统监控（操作日志、登录日志、定时任务、服务监控）
- 系统工具（代码生成、表单构建）
- 商城管理（订单、商品、店铺、用户）

---

## 常见问题

### Q1：登录验证码加载失败 `TypeError: Cannot read property 'baseUrl' of undefined`
**A**：`pet_system_app/api/login.js` 导入 `config` 的方式与 `config.js` 的导出方式不匹配。已统一为 `import config from '@/config'`，并保证 `config.js` 同时使用 `module.exports` 与 `export default` 导出。

### Q2：数据库导入时报错 "table already exists"
**A**：商城脚本 `add_mall_table.sql` 包含字段补充和表创建，请确保已先成功执行 `pet_vue.sql`，且数据库中没有旧版本的同名表冲突。

### Q3：微信小程序请求后端失败
**A**：
1. `config.js` 中 `baseUrl` 必须使用局域网 IP，不能是 `localhost`
2. 微信开发者工具勾选 `不校验合法域名`
3. 生产环境需要在微信公众平台配置 request 合法域名

---

## 许可证

- 移动端：[pet_system_app/LICENSE](pet_system_app/LICENSE)
- PC 端：[pet_system_pc/LICENSE](pet_system_pc/LICENSE)

基础框架基于 [RuoYi](http://ruoyi.vip) 开源协议。
