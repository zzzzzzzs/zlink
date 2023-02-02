---
title: 开发者指南
redirectFrom: /guide/dev-guide.html
---

## 前置条件

在搭建 zlink 开发环境之前请确保你已经安装如下软件

- Git:版本控制软件
- JDK：后端开发
- Maven：Java包管理
- Node:前端开发;

### 环境要求

| 环境    | 版本         |
| ------- | ------------ |
| npm     | 6.14.17      |
| node.js | 14.21.2      |
| jdk     | 11           |
| maven   | 3.6.0+       |
| lombok  | IDEA插件安装 |
| mysql   | 8.0+         |

### 克隆代码

```shell
git clone https://github.com/zzzzzzzs/zlink.git
```

## IntelliJ IDEA

IntelliJ IDEA 来进行 zlink 前后端开发。Eclipse 为测试过。

### 安装 Lombok 插件

IDEA 提供了插件设置来安装 Lombok 插件。如果尚未安装，请在导入 zlink 之前按照以下说明来进行操作以启用对 Lombok 注解的支持：

1. 转到 IDEA Settings → Plugins 并选择 Marketplace 。
2. 选择并安装 Lombok 插件。
3. 如果出现提示，请重启 IDEA 。

### 导入 zlink

1. 启动 IDEA 并选择 Open。
2. 选择已克隆的 zlink 存储库的根文件夹。
3. 等待项目加载完成。
4. 设置 JDK 11 和 Maven 3.6.0

## 前端环境

### 安装 node.js

可用版本 14.21.2 +，安装步骤详情百度。

### 安装 npm

因 node.js 安装后 npm 版本较高，因此需要可用版本 6.14.17，升级npm命令如下：

```shell
npm install npm@6.14.17 -g
```

### 初始化前端依赖

进入 `zlink\zlink-web` 目录下执行

```shell
npm install
```

运行 web 项目

```shell
npm run serve
```

## 初始化数据库

首先创建 `zlink `  数据库

```sql
CREATE DATABASE `zlink` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci'
```

然后在数据库中执行 `zlink/zlink-doc/db/zlink.sql` 文件中的内容

## 打包

首先在 `zlink/zlink-web` 目录下执行

```shell
npm install
npm run build
```

生成 `dist` 文件，然后打包后端项目，会直接将生成 `dist` 目录下的文件拷贝到 `zlink/zlink-admin/src/main/resources/static` 目录下

```shell
mvn clean install -Dmaven.test.skip=true
```

然后使用 `zlink.sh` 脚本启动，访问 `http://ip:5465` 访问就可以了

## 源码结构

### zlink-admin

zlink 的管理中心，标准的 SpringBoot 应用，负责与前端交互。

### zlink-alert

zlink 的告警中心，支持钉钉 、企业微信 、飞书 、邮箱。

### zlink-cdc

zlink 的 cdc 部分，主要是用来生成 cdc sql。

### zlink-common

zlink 的子项目的公用类及实现项目。

### zlink-doc

zlink 文档部分

### zlink-flink

zlink 的 flinksql 部分

### zlink-metadata

zlink 元数据中心部分，支持 SPI 方式

### zlink-web

zlink 前端项目

### assembly

项目打包配置，生产最终的 tar.gz
