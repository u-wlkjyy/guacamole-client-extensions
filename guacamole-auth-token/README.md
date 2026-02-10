# guacamole-auth-token

基于 **token 参数** 的 Guacamole 认证扩展（非文件版）。

该扩展从请求中读取 `token`，再按模块内部逻辑解析 token 内容并构造用户/连接数据。

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

## 3. guacamole.properties 配置

当前代码中该模块 **没有额外必填配置项**（未定义类似 `auth-file-dir` 的属性键）。

最小配置通常只需确保 Guacamole 基础配置可用，并加载该扩展 jar。

## 4. 访问方式

使用带 `token` 参数的 URL，例如：

```text
https://your-guacamole/#/?token=your-token-value
```

## 5. 与 file-token 的区别

- `guacamole-auth-token`：不依赖本地 token JSON 文件目录。
- `guacamole-auth-file-token`：依赖 `auth-file-dir`，通过 `<token>.json` 提供用户和连接。

## 6. 排查

- URL 无 `token` 参数：认证会失败。
- token 内容不合法：认证失败并在日志出现错误。
- 若与其他认证扩展并存，需确认扩展加载顺序和认证链行为是否符合预期。
