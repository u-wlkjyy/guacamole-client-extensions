# guacamole-auth-file-token

基于 **token + 本地 JSON 文件** 的 Guacamole 认证扩展。

该扩展会从登录请求参数里读取 `token`，并在配置目录中查找同名 JSON 文件（`<token>.json`），解析后生成会话用户和连接列表。

## 1. 构建

在模块目录执行：

```bash
mvn clean package
```

产物通常在：

- `target/guacamole-auth-file-token-*.jar`

## 2. 部署

将 jar 复制到 `GUACAMOLE_HOME/extensions`：

```bash
cp target/guacamole-auth-file-token-*.jar /etc/guacamole/extensions/
```

> `GUACAMOLE_HOME` 也可能是容器内的 `/etc/guacamole`，按你的部署方式调整。

## 3. guacamole.properties 配置

该扩展要求一个必填配置项：

```properties
auth-file-dir: /etc/guacamole/auth-file-token
```

说明：

- `auth-file-dir`：token JSON 文件所在目录。

## 4. token JSON 格式示例

当用户访问：

- `https://your-guacamole/#/?token=demo-token`

扩展会读取：

- `/etc/guacamole/auth-file-token/demo-token.json`

示例内容（按当前代码可用字段）：

```json
{
  "username": "demo-user",
  "connections": [
    {
      "name": "My RDP",
      "protocol": "rdp",
      "parameters": {
        "hostname": "10.0.0.10",
        "port": "3389",
        "username": "administrator",
        "password": "Passw0rd!"
      }
    }
  ]
}
```

> 建议每个 token 只对应一个短时文件，并在使用后及时清理。

## 5. 访问方式

使用带 `token` 参数的 URL 登录 Guacamole，例如：

```text
https://your-guacamole/#/?token=demo-token
```

## 6. 排查

- 未配置 `auth-file-dir`：扩展无法初始化。
- token 不存在：对应 `<token>.json` 找不到时认证失败。
- JSON 格式错误：日志中会出现解析异常。
- 路径穿越会被拦截：例如 `../` 形式 token 会被拒绝。
