<div align="center">
<h1>BurpOpTools</h1>

<p>Burp Suite 常用工具集快捷菜单插件</p>

<p>
  <a href="https://mit-license.org/">
    <img src="https://img.shields.io/github/license/TLDRO/BurpOpTools?style=flat" alt="License">
  </a>
  <a href="https://github.com/TLDRO/BurpOpTools">
    <img src="https://img.shields.io/github/stars/TLDRO/BurpOpTools?style=flat" alt="Stars">
  </a>
  <a href="https://github.com/TLDRO/BurpOpTools">
    <img src="https://img.shields.io/github/forks/TLDRO/BurpOpTools?style=flat" alt="Forks">
  </a>
  <a href="https://github.com/TLDRO/BurpOpTools/releases">
    <img src="https://img.shields.io/github/v/release/TLDRO/BurpOpTools?sort=semver" alt="Release">
  </a>
</p>

<div>

中文 ｜ [English](README.md)

</div>
</div>  

---

## 📖 项目介绍

BurpOpTools 是一个功能强大的 Burp Suite 扩展插件，提供了丰富的编码/解码、HTTP 请求格式转换和数据处理功能，旨在提升安全测试和运维效率。

本项目基于 Burp Suite Montoya API 开发，是对该 API 的一次学习和简单实现。通过在右键菜单中实现日常安全测试中常用的编码转换、格式化处理等功能，并扩展 Burp Suite 原生功能，深入理解 Burp Suite 扩展开发的核心机制，同时为安全测试人员提供实用工具。

## ✨ 主要功能

### 🔐 编码功能

- **Base64 编码** - 标准 Base64 编码
- **HEX 编码（16进制）** - 十六进制编码
- **Unicode 编码** - 全字符 Unicode 编码
- **Unicode 编码（忽略ASCII字符）** - 仅编码非 ASCII 字符
- **Unicode 编码（JSON键值编码）** - 对 JSON 键值进行 Unicode 编码，保持 JSON 结构
- **URL 编码** - 完整 URL 编码
- **URL 编码（特殊符号）** - 仅编码特殊字符
- **UTF-8 编码（\x十六进制）** - UTF-8 转 \x 格式十六进制
- **UTF-16LE 编码（16进制）** - UTF-16 Little-Endian 十六进制编码

### 🔓 解码功能

- **Base64 解码** - 标准 Base64 解码
- **HEX 解码（16进制）** - 十六进制解码
- **Unicode 解码** - Unicode 字符解码
- **URL 解码** - URL 解码

### 📝 格式化

- **JSON 压缩** - 移除 JSON 多余空白
- **JSON 格式化** - 美化 JSON 结构
- **XML 压缩** - 移除 XML 多余空白
- **XML 格式化** - 美化 XML 结构

### 🔄 HTTP 请求修改

支持直接修改 HTTP 请求格式（不经过预览窗口，保留完整请求头）：

> **说明**：前三项功能在 Burp Suite 的 "Change body encoding" 中已存在，此处实现仅用于学习和测试。`转换为 XML-POST 参数`为在此基础上的扩展实现。

- **转换为普通 POST 参数** - application/x-www-form-urlencoded
- **转换为 JSON-POST 参数** - application/json
- **修改为上传数据包** - multipart/form-data
- **转换为 XML-POST 参数** - application/xml（扩展功能）

## 🎯 特色功能

### 智能预览
- 编码/解码操作提供预览窗口
- 支持一键复制结果到剪贴板
- 确认后再替换，安全可靠

### HTTP 请求智能转换
- 自动解析多种格式（URL-encoded、JSON、Multipart）
- 保留原始请求行和请求头
- 自动计算 Content-Length
- 直接应用修改，无需预览

### 国际化支持
- 根据系统时区自动识别语言
- 支持中文和英文界面

## 📦 安装

### 方法一：编译安装

1. **克隆项目**
```bash
git clone https://github.com/TLDRO/BurpOpTools.git
cd BurpOpTools
```

2. **编译插件**
```bash
./gradlew clean build
```

3. **加载插件**
- 打开 Burp Suite
- 进入 `Extensions` → `Add`
- 选择 `build/libs/BurpOpTools-1.0.4.jar`
- 点击 `Next` 完成安装

### 方法二：直接下载

从 [Releases](https://github.com/TLDRO/BurpOpTools/releases) 页面下载最新版本的 JAR 文件，然后在 Burp Suite 中加载。

## 🚀 使用方法

### 编码/解码操作

1. 在 Burp Suite 的任意 HTTP 消息编辑器中（Repeater、Proxy History 等）
2. 选中需要处理的文本
3. 右键点击 → `BurpOpTools`
4. 选择相应的编码或解码功能
5. 在预览窗口中查看结果
6. 点击"替换"应用更改，或"复制结果"复制到剪贴板

### HTTP 请求格式转换

1. 在 HTTP 请求编辑器中（无需选中文本）
2. 右键点击 → `BurpOpTools` → `HTTP 请求修改`
3. 选择目标格式（如“转换为 JSON-POST 参数”）
4. 请求会立即转换为目标格式（直接应用，无预览）

### 格式化操作

1. 在 HTTP 消息编辑器中选中需要格式化的内容（完整的 JSON 或 XML）
2. 右键点击 → `BurpOpTools` → `HTTP 请求修改` → `格式化`
3. 选择对应功能：
   - **JSON 格式化** - 美化 JSON，增加缩进和换行
   - **JSON 压缩** - 移除 JSON 中的多余空白和换行
   - **XML 格式化** - 美化 XML 结构
   - **XML 压缩** - 移除 XML 中的多余空白
4. 在预览窗口中查看结果，点击“替换”应用更改

### JSON 键值 Unicode 编码

对完整 JSON 的键和值进行 Unicode 编码，同时保持 JSON 结构不变，编码后仍可正常解析。

1. 选中完整的 JSON 文本
2. 右键 → `BurpOpTools` → `编码` → `Unicode 编码（JSON键值编码）`
3. JSON 的键和值会被编码，但 JSON 结构保持不变

## 💡 使用场景示例

### 场景 1: UTF-8 编码 - Burp Suite Proxy 中文搜索与 Intruder 响应匹配

UTF-8 编码功能可用于 Burp Suite 的 Proxy 模块进行中文关键词搜索，以及 Intruder 模块的响应匹配。

**示例：搜索中文关键词 "成功"**
```
原始文本: 成功
UTF-8 编码: \xe6\x88\x90\xe5\x8a\x9f
```

在 Burp Suite Proxy 的搜索框或 Intruder 的 Grep-Match 中使用编码后的十六进制值即可匹配中文内容。

**详细用法请参考**: [Burp Suite 使用技巧](https://tldro.github.io/tips-burpsuite)

### 场景 2: Unicode 编码（JSON键值编码）- 绕过 WAF JSON 过滤

对 JSON 键值进行 Unicode 编码，保持 JSON 结构完整，可用于绕过某些 WAF 对 JSON 关键字的检测。

**示例：**
```json
// 编码前
{"name": "张三", "city": "北京"}

// 编码后（所有字符均被编码）
{"\u006e\u0061\u006d\u0065": "\u5f20\u4e09", "\u0063\u0069\u0074\u0079": "\u5317\u4eac"}
```

编码后的 JSON 仍可被服务器正常解析，但部分 WAF 可能无法识别已编码的恶意 payload。

### 场景 3: UTF-16LE 编码 - CVE-2025-66487 React2Shell RCE Bypass WAF

利用 UTF-16LE 编码绕过 WAF 对 React Server Components 的恶意 payload 检测。

**CVE-2025-66487 漏洞利用示例：**
```
原始 Payload:
{"type":"$","key":null,"ref":null,"props":{"is":"script","children":"alert(1)"}}

UTF-16LE 十六进制编码:
7b0022007400790070006500220...（完整编码后的十六进制字符串）
```

**使用步骤：**
1. 选中恶意 payload 文本
2. 右键 → `BurpOpTools` → `编码` → `UTF-16LE 编码（16进制）`
3. 将编码结果作为请求体发送
4. **重要：需手动设置请求头** `Content-Type: text/plain; charset=utf-16le`

通过 UTF-16LE 编码，WAF 可能无法正确解析 payload，但目标应用服务器可以正常处理，从而实现 WAF 绕过。

## 🛠️ 技术栈

- **语言**: Java
- **构建工具**: Gradle 8.11.1
- **依赖**:
  - Burp Suite Montoya API 2023.12.1
  - Gson 2.10.1
  - JUnit 5.10.0

## 📂 项目结构

```
BurpOpTools/
├── src/main/java/org/example/
│   ├── i18n/
│   │   └── I18n.java                    # 国际化支持
│   ├── utils/
│   │   ├── DecoderUtils.java            # 解码工具
│   │   ├── EncoderUtils.java            # 编码工具
│   │   ├── HttpRequestConverter.java    # HTTP 请求转换
│   │   ├── JsonProcessor.java           # JSON 处理
│   │   └── XmlProcessor.java            # XML 处理
│   ├── BurpOpToolsContextMenuProvider.java  # 右键菜单提供者
│   ├── BurpOpToolsExtension.java            # 插件主类
│   └── PreviewDialog.java               # 预览对话框
├── build.gradle                         # Gradle 配置
└── README.md                            # 本文档
```

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 [MIT 许可证](./LICENSE)。

## 🙏 致谢

- Burp Suite 团队提供的优秀 Montoya API
- Gson 库提供的 JSON 处理支持
- [Yakit](https://github.com/yaklang/yakit) 项目提供的灵感和参考

## 💖 支持项目

如果这个项目对您有帮助，请考虑给它一个 ⭐ ！

或者分享给您的朋友，以帮助它得到改善！

---

<div align="center">

Made with ❤️ by [TLDRO](https://github.com/TLDRO)

</div>
