# guacamole-auth-token

基于 **token 参数** 的 Guacamole 认证扩展（非文件版）。

该扩展从请求中读取 `token`，并调用外部校验服务验证后构造用户/连接数据。

## 1. 构建

在模块目录执行：

```bash
mvn clean package
```

产物通常在：

- `target/guacamole-auth-token-*.jar`

## 2. 部署

将 jar 复制到 `GUACAMOLE_HOME/extensions`：

```bash
cp target/guacamole-auth-token-*.jar /etc/guacamole/extensions/
```

## 3. guacamole.properties 配置（必填）

该模块 **不是零配置**，必须在 `guacamole.properties` 中配置以下必填项：

```properties
cvmlab-auth-url: http://127.0.0.1:8000/verify
cvmlab-auth-token: REPLACE_WITH_SHARED_SECRET
```

说明：

- `cvmlab-auth-url`：外部 token 校验接口地址。
- `cvmlab-auth-token`：Guacamole 调用校验接口时使用的共享密钥。

> 以上任一项缺失都会导致认证失败。

## 4. 访问方式

使用带 `token` 参数的 URL，例如：

```text
https://your-guacamole/#/?token=your-token-value
```

## 5. 与 file-token 的区别

- `guacamole-auth-token`：依赖外部校验接口（`cvmlab-auth-url`）。
- `guacamole-auth-file-token`：依赖本地 token 文件目录（`cvmlab.auth.dir`）。

## 6. 排查

- 未配置 `cvmlab-auth-url` / `cvmlab-auth-token`：认证失败。
- URL 无 `token` 参数：认证会失败。
- token 不合法或校验接口异常：认证失败并在日志出现错误。
- 若与其他认证扩展并存，需确认扩展加载顺序和认证链行为是否符合预期。
