# guacamole-display-watermark

Guacamole 前端显示增强扩展，用于注入水印/状态展示相关静态资源。

当前模块通过 `guac-manifest.json` 注入：

- `watermark.css`
- `network-status.js`

## 1. 构建

在模块目录执行：

```bash
mvn clean package
```

产物通常在：

- `target/guacamole-display-watermark-*.jar`

## 2. 部署

将 jar 复制到 `GUACAMOLE_HOME/extensions`：

```bash
cp target/guacamole-display-watermark-*.jar /etc/guacamole/extensions/
```

## 3. 配置

当前模块不依赖 `guacamole.properties` 的额外配置项。

样式和行为主要由以下文件决定：

- `src/main/resources/watermark.css`
- `src/main/resources/network-status.js`

## 4. 生效验证

部署后刷新浏览器（必要时清空缓存）并登录 Guacamole，检查：

- 页面是否加载了水印样式。
- 页面是否出现网络状态脚本相关行为。

## 5. 定制方式

- 改水印样式：编辑 `watermark.css`。
- 改脚本逻辑：编辑 `network-status.js`。
- 修改后重新构建并替换 jar 即可。
