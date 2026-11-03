# AIDemo 工作区结构

本工作区包含三个相互独立的子项目：

```
AIDemo/
├── pom.xml / src/          # ① 原 Spring AI RAG Demo（端口 8081，独立运行）
├── stock-list-backend/     # ② 囤货清单后端（Java 21 + Spring Boot 3.3.5 + JPA + H2，端口 8090）
└── stock-list-miniprogram/ # ③ 囤货清单小程序前端（uni-app Vue3 + Vite，编译目标 mp-weixin）
```

## 启动方式

### 后端 stock-list-backend

- IDEA：等待 Maven 导入完成后，直接运行运行配置 **StockListApplication**（或主类 `com.example.stocklist.StockListApplication`）
- 命令行：`cd stock-list-backend && mvn spring-boot:run`
- 验证：浏览器打开 `http://localhost:8090/swagger-ui.html`
- 数据：H2 文件数据库自动创建在 `stock-list-backend/data/stockdb.mv.db`，删除该目录即重置数据

> 若 IDEA 未识别 `stock-list-backend` 模块：右键 `stock-list-backend/pom.xml` → **Add as Maven Project**

### 前端 stock-list-miniprogram

```bash
cd stock-list-miniprogram
npm install          # 已安装过可跳过
npm run dev:mp-weixin
```

微信开发者工具导入 `stock-list-miniprogram/dist/dev/mp-weixin`，并在「详情 → 本地设置」勾选 **不校验合法域名**（本地调试访问 http://localhost:8090）。

正式上线前在 `src/manifest.json` 的 `mp-weixin.appid` 填入自己的小程序 AppID。

### 核心功能

- 库存录入：品类、名称、库存数量、单位、有效期、存放地点、单品阈值、备注
- 购买提醒：库存 ≤ 生效阈值（单品阈值，未设置则用全局默认）→「库存不足」；有效期距今 ≤ 预警天数 →「临期」
- 全局阈值与预警天数在小程序「设置」页维护
