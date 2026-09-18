# SCT_MSQ

SCT（Sky City Server）Minecraft 服务器官网。

前后端分离：`SCT/` 是 Spring Boot 后端，`SCT_MSQ/` 是 Vue 3 前端，`sql/` 放数据库脚本。

---

## 目录

- [快速开始](#快速开始)
  - [1. 初始化数据库](#1-初始化数据库)
  - [2. 启动后端](#2-启动后端)
  - [3. 启动前端](#3-启动前端)
  - [4. 访问地址](#4-访问地址)
- [配置说明](#配置说明)
  - [`application.yml`（公共配置）](#applicationyml公共配置)
  - [`application-dev.yml`（dev 环境配置）](#application-devymldev-环境配置)
- [留言接口开发文档](#留言接口开发文档)
  - [密钥配置](#密钥配置)
  - [留言接入示例](#留言接入示例)

---

## 快速开始

### 1. 初始化数据库

**全新环境**：直接导入全量脚本。

```bash
mysql -h <host> -P 3306 -u root -p -e "CREATE DATABASE IF NOT EXISTS sct DEFAULT CHARACTER SET utf8mb4;"
mysql -h <host> -P 3306 -u root -p sct < sql/sct.sql
```

### 2. 启动后端

先改 `SCT/sct-server/src/main/resources/application-dev.yml`，把数据库、Redis(没用上，可以不配置)、上传路径改成你本机的（见[配置说明](#配置说明)）。

```bash
cd SCT

# 首次需要先把 sct-common / sct-pojo 装进本地仓库
mvn -DskipTests clean install

# 再启动 sct-server
cd sct-server
mvn spring-boot:run
```

或者在 IDEA 里：先对父工程 `SCT/pom.xml` 执行一次 Maven `install`，然后直接运行 `SctApplication`。

指定端口启动（避免 8080 被占用）：

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=18080
```

> 注意：`spring-boot:run` 必须在 `SCT/sct-server` 目录下执行。在父工程 `SCT/` 下执行会报 `Unable to find a suitable main class`。

打包成可执行 jar：

```bash
cd SCT && mvn -DskipTests clean package
java -jar sct-server/target/sct-server-1.0-SNAPSHOT.jar
```

### 3. 启动前端

```bash
cd SCT_MSQ
npm install
npm run dev
```

生产构建：

```bash
npm run build      # 产物在 SCT_MSQ/dist
npm run preview    # 本地预览构建产物
```

### 4. 访问地址

| 地址 | 说明 |
| --- | --- |
| http://localhost:5173 | 前端首页 |
| http://localhost:5173/admin | 管理后台 |
| http://localhost:8080 | 后端接口根地址 |
| http://localhost:8080/doc.html | Knife4j 接口文档 |
| http://localhost:8080/profile/... | 本地上传文件的静态访问路径 |

后台账号在 `admin_user` 表里，初始数据只有一条 `admin`，密码是 `123456`。

---

## 配置说明

### `application.yml`（公共配置）

- `server.port`：后端端口，默认 `8080`
- `spring.profiles.active`：默认 `dev`
- `sct.jwt.admin-secret-key` / `admin-ttl`：后台 JWT 密钥与有效期
- `sct.jwt.admin-token-name`：`Authorization`，前端把 token 放在这个请求头里
- `mybatis-plus.global-config.db-config.logic-delete-field`：`deleted`，所有删除都是逻辑删除
- `oss.type`：`0` 本地存储，`1` S3 存储

### `application-dev.yml`（dev 环境配置）

```yaml
sct:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: <数据库地址>
    port: 3306
    database: sct
    username: <用户名>
    password: <密码>
  profile: <本地上传目录>     # Windows 例：D:/sct_msq_upload/sct/upload
                             # macOS/Linux 例：/Users/xxx/sct/upload
  s3:
    endpoint: <S3 地址>
    access-key-id: <AK>
    secret-access-key: <SK>
    bucket-name: <桶名>
    path-style-access: true   # MinIO / RUSTFS 等兼容服务需要 true
  redis:
    host: 127.0.0.1
    port: 6379
    database: 2

oss:
  type: 0    # 0 本地存储，1 S3
```

---

## 留言接口开发文档

### 密钥配置

发布留言接口用 `config` 表里 `config_key = 'message_api_key'` 的 `config_value` 做鉴权。

初始值在 `sql/sct.sql` 和 `sql/member_message.sql` 里都是 `0b6f36caf01fbbc228203574dbb8bf6f`，**上线前请换成自己的随机串**。

密钥为空或记录不存在时，发布接口会直接拒绝，返回 `留言发布接口密钥未配置，请先在 config 表中添加 message_api_key`。

---

### 留言接入示例

**Python**

```python
import requests

API = "http://localhost:8080/message/publish"
KEY = "0b6f36caf01fbbc228203574dbb8bf6f"   # 与 config.message_api_key 一致

def publish(qq: str, content: str, player_id: str | None = None):
    body = {"qq": qq, "content": content}
    if player_id:
        body["playerId"] = player_id
    resp = requests.post(API, json=body, headers={"X-Api-Key": KEY}, timeout=5)
    data = resp.json()
    if data.get("code") != 0:
        # 失败时 data["message"] 是中文提示，可直接回给玩家
        print("发布失败:", data.get("message"))
        return None
    return data["data"]   # 新留言 id
```
