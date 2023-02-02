---
title: 安装部署
redirectFrom: /guide/install-deploy.html
---

## 下载

[下载链接](https://github.com/zzzzzzzs/zlink/releases)

## 软件需求

- Linux 操作系统
- Java 11

## zlink 部署

### 解压文件

```shell
tar -zxvf zlink-release-{version}.tar.gz
```

文件大纲

```shell
|-- bin
|   `-- zlink.sh
|-- config
|   |-- application.yml
|   |-- db
|   |   `-- zlink.sql
|   `-- log4j2.xml
`-- lib
    |-- HikariCP-4.0.3.jar
    |-- accessors-smart-2.4.8.jar
    |-- akka-actor_2.12-2.5.21.jar
    |-- akka-protobuf_2.12-2.5.21.jar
    |-- akka-slf4j_2.12-2.5.21.jar
    |-- akka-stream_2.12-2.5.21.jar
    |-- aopalliance-1.0.jar
    |-- asm-9.1.jar
    |-- audience-annotations-0.5.0.jar
    |-- avatica-core-1.17.0.jar
    |-- avro-1.7.7.jar
    |-- awaitility-4.2.0.jar
    |-- checker-qual-3.12.0.jar
    |-- chill-java-0.7.6.jar
    |-- chill_2.12-0.7.6.jar
    |-- commons-beanutils-1.9.3.jar
    |-- commons-cli-1.3.1.jar
    |-- commons-codec-1.15.jar
    |-- commons-collections-3.2.2.jar
    |-- commons-compiler-3.1.8.jar
    |-- commons-compress-1.18.jar
    |-- commons-configuration2-2.1.1.jar
    |-- commons-daemon-1.0.13.jar
    |-- commons-io-2.5.jar
    |-- commons-lang-2.6.jar
    |-- commons-lang3-3.12.0.jar
    |-- commons-logging-1.1.3.jar
    |-- commons-math3-3.5.jar
    |-- commons-net-3.6.jar
    |-- config-1.3.3.jar
    |-- curator-client-2.13.0.jar
    |-- curator-framework-2.13.0.jar
    |-- curator-recipes-2.13.0.jar
    |-- druid-1.2.8.jar
    |-- druid-spring-boot-starter-1.2.8.jar
    |-- error_prone_annotations-2.11.0.jar
    |-- failureaccess-1.0.1.jar
    |-- flink-annotations-1.13.6.jar
    |-- flink-clients_2.12-1.13.6.jar
    |-- flink-connector-base-1.13.6.jar
    |-- flink-connector-files-1.13.6.jar
    |-- flink-connector-jdbc_2.12-1.13.6.jar
    |-- flink-core-1.13.6.jar
    |-- flink-file-sink-common-1.13.6.jar
    |-- flink-hadoop-fs-1.13.6.jar
    |-- flink-java-1.13.6.jar
    |-- flink-metrics-core-1.13.6.jar
    |-- flink-optimizer_2.12-1.13.6.jar
    |-- flink-queryable-state-client-java-1.13.6.jar
    |-- flink-runtime-web_2.12-1.13.6.jar
    |-- flink-runtime_2.12-1.13.6.jar
    |-- flink-scala_2.12-1.13.6.jar
    |-- flink-shaded-asm-7-7.1-13.0.jar
    |-- flink-shaded-guava-18.0-13.0.jar
    |-- flink-shaded-jackson-2.12.1-13.0.jar
    |-- flink-shaded-netty-4.1.49.Final-13.0.jar
    |-- flink-shaded-zookeeper-3-3.4.14-13.0.jar
    |-- flink-sql-connector-mysql-cdc-2.2.1.jar
    |-- flink-streaming-java_2.12-1.13.6.jar
    |-- flink-streaming-scala_2.12-1.13.6.jar
    |-- flink-table-api-java-1.13.6.jar
    |-- flink-table-api-java-bridge_2.12-1.13.6.jar
    |-- flink-table-api-scala-bridge_2.12-1.13.6.jar
    |-- flink-table-api-scala_2.12-1.13.6.jar
    |-- flink-table-common-1.13.6.jar
    |-- flink-table-planner-blink_2.12-1.13.6.jar
    |-- flink-table-runtime-blink_2.12-1.13.6.jar
    |-- flink-yarn_2.12-1.13.6.jar
    |-- force-shading-1.13.6.jar
    |-- grizzled-slf4j_2.12-1.3.2.jar
    |-- gson-2.9.1.jar
    |-- guava-31.1-jre.jar
    |-- guice-4.0.jar
    |-- guice-servlet-4.0.jar
    |-- hadoop-annotations-3.1.3.jar
    |-- hadoop-auth-3.1.3.jar
    |-- hadoop-common-3.1.3.jar
    |-- hadoop-hdfs-3.1.3.jar
    |-- hadoop-hdfs-client-3.1.3.jar
    |-- hadoop-mapreduce-client-core-3.1.3.jar
    |-- hadoop-yarn-api-3.1.3.jar
    |-- hadoop-yarn-client-3.1.3.jar
    |-- hadoop-yarn-common-3.1.3.jar
    |-- hamcrest-2.2.jar
    |-- htrace-core4-4.1.0-incubating.jar
    |-- httpclient-4.5.13.jar
    |-- httpcore-4.4.15.jar
    |-- hutool-all-5.8.9.jar
    |-- j2objc-annotations-1.3.jar
    |-- jackson-annotations-2.13.4.jar
    |-- jackson-core-2.13.4.jar
    |-- jackson-core-asl-1.9.2.jar
    |-- jackson-databind-2.13.4.jar
    |-- jackson-datatype-jdk8-2.13.4.jar
    |-- jackson-datatype-jsr310-2.13.4.jar
    |-- jackson-jaxrs-1.9.2.jar
    |-- jackson-jaxrs-base-2.13.4.jar
    |-- jackson-jaxrs-json-provider-2.13.4.jar
    |-- jackson-mapper-asl-1.9.2.jar
    |-- jackson-module-jaxb-annotations-2.13.4.jar
    |-- jackson-module-parameter-names-2.13.4.jar
    |-- jackson-xc-1.9.2.jar
    |-- jakarta.activation-api-1.2.2.jar
    |-- jakarta.annotation-api-1.3.5.jar
    |-- jakarta.xml.bind-api-2.3.3.jar
    |-- janino-3.1.8.jar
    |-- javassist-3.24.0-GA.jar
    |-- javax.activation-api-1.2.0.jar
    |-- javax.annotation-api-1.3.2.jar
    |-- javax.inject-1.jar
    |-- javax.servlet-api-4.0.1.jar
    |-- jaxb-api-2.3.1.jar
    |-- jaxb-impl-2.2.3-1.jar
    |-- jcip-annotations-1.0-1.jar
    |-- jersey-client-1.19.jar
    |-- jersey-core-1.19.jar
    |-- jersey-guice-1.19.jar
    |-- jersey-json-1.19.jar
    |-- jersey-server-1.19.jar
    |-- jersey-servlet-1.19.jar
    |-- jettison-1.1.jar
    |-- jetty-http-9.4.49.v20220914.jar
    |-- jetty-io-9.4.49.v20220914.jar
    |-- jetty-security-9.4.49.v20220914.jar
    |-- jetty-server-9.4.49.v20220914.jar
    |-- jetty-servlet-9.4.49.v20220914.jar
    |-- jetty-util-9.4.49.v20220914.jar
    |-- jetty-util-ajax-9.4.49.v20220914.jar
    |-- jetty-webapp-9.4.49.v20220914.jar
    |-- jetty-xml-9.4.49.v20220914.jar
    |-- jline-0.9.94.jar
    |-- jsch-0.1.54.jar
    |-- json-smart-2.4.8.jar
    |-- jsp-api-2.1.jar
    |-- jsqlparser-3.2.jar
    |-- jsr305-3.0.2.jar
    |-- jsr311-api-1.1.1.jar
    |-- jul-to-slf4j-1.7.36.jar
    |-- kerb-admin-1.0.1.jar
    |-- kerb-client-1.0.1.jar
    |-- kerb-common-1.0.1.jar
    |-- kerb-core-1.0.1.jar
    |-- kerb-crypto-1.0.1.jar
    |-- kerb-identity-1.0.1.jar
    |-- kerb-server-1.0.1.jar
    |-- kerb-simplekdc-1.0.1.jar
    |-- kerb-util-1.0.1.jar
    |-- kerby-asn1-1.0.1.jar
    |-- kerby-config-1.0.1.jar
    |-- kerby-pkix-1.0.1.jar
    |-- kerby-util-1.0.1.jar
    |-- kerby-xdr-1.0.1.jar
    |-- kryo-2.24.0.jar
    |-- leveldbjni-all-1.8.jar
    |-- listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar
    |-- log4j-1.2.17.jar
    |-- log4j-api-2.17.2.jar
    |-- log4j-core-2.17.2.jar
    |-- log4j-jul-2.17.2.jar
    |-- log4j-slf4j-impl-2.17.2.jar
    |-- lombok-1.18.24.jar
    |-- lz4-java-1.6.0.jar
    |-- minlog-1.2.jar
    |-- mybatis-3.5.6.jar
    |-- mybatis-plus-3.4.1.jar
    |-- mybatis-plus-annotation-3.4.1.jar
    |-- mybatis-plus-boot-starter-3.4.1.jar
    |-- mybatis-plus-core-3.4.1.jar
    |-- mybatis-plus-extension-3.4.1.jar
    |-- mybatis-plus-generator-3.4.1.jar
    |-- mybatis-spring-2.0.5.jar
    |-- mysql-connector-java-8.0.22.jar
    |-- netty-3.10.5.Final.jar
    |-- netty-all-4.1.84.Final.jar
    |-- netty-buffer-4.1.84.Final.jar
    |-- netty-codec-4.1.84.Final.jar
    |-- netty-codec-dns-4.1.84.Final.jar
    |-- netty-codec-haproxy-4.1.84.Final.jar
    |-- netty-codec-http-4.1.84.Final.jar
    |-- netty-codec-http2-4.1.84.Final.jar
    |-- netty-codec-memcache-4.1.84.Final.jar
    |-- netty-codec-mqtt-4.1.84.Final.jar
    |-- netty-codec-redis-4.1.84.Final.jar
    |-- netty-codec-smtp-4.1.84.Final.jar
    |-- netty-codec-socks-4.1.84.Final.jar
    |-- netty-codec-stomp-4.1.84.Final.jar
    |-- netty-codec-xml-4.1.84.Final.jar
    |-- netty-common-4.1.84.Final.jar
    |-- netty-handler-4.1.84.Final.jar
    |-- netty-handler-proxy-4.1.84.Final.jar
    |-- netty-resolver-4.1.84.Final.jar
    |-- netty-resolver-dns-4.1.84.Final.jar
    |-- netty-resolver-dns-classes-macos-4.1.84.Final.jar
    |-- netty-resolver-dns-native-macos-4.1.84.Final-osx-aarch_64.jar
    |-- netty-resolver-dns-native-macos-4.1.84.Final-osx-x86_64.jar
    |-- netty-transport-4.1.84.Final.jar
    |-- netty-transport-classes-epoll-4.1.84.Final.jar
    |-- netty-transport-classes-kqueue-4.1.84.Final.jar
    |-- netty-transport-native-epoll-4.1.84.Final-linux-aarch_64.jar
    |-- netty-transport-native-epoll-4.1.84.Final-linux-x86_64.jar
    |-- netty-transport-native-kqueue-4.1.84.Final-osx-aarch_64.jar
    |-- netty-transport-native-kqueue-4.1.84.Final-osx-x86_64.jar
    |-- netty-transport-native-unix-common-4.1.84.Final.jar
    |-- netty-transport-rxtx-4.1.84.Final.jar
    |-- netty-transport-sctp-4.1.84.Final.jar
    |-- netty-transport-udt-4.1.84.Final.jar
    |-- nimbus-jose-jwt-4.41.1.jar
    |-- objenesis-2.1.jar
    |-- okhttp-2.7.5.jar
    |-- okio-1.6.0.jar
    |-- paranamer-2.3.jar
    |-- postgresql-42.2.14.jar
    |-- protobuf-java-2.5.0.jar
    |-- re2j-1.1.jar
    |-- reactive-streams-1.0.4.jar
    |-- reload4j-1.2.19.jar
    |-- scala-compiler-2.12.7.jar
    |-- scala-java8-compat_2.12-0.8.0.jar
    |-- scala-library-2.12.7.jar
    |-- scala-parser-combinators_2.12-1.1.1.jar
    |-- scala-reflect-2.12.7.jar
    |-- scala-xml_2.12-1.0.6.jar
    |-- scopt_2.12-3.5.0.jar
    |-- slf4j-api-1.7.36.jar
    |-- slf4j-reload4j-1.7.36.jar
    |-- snakeyaml-1.30.jar
    |-- snappy-java-1.1.8.3.jar
    |-- spring-aop-5.3.23.jar
    |-- spring-beans-5.3.23.jar
    |-- spring-boot-2.7.5.jar
    |-- spring-boot-autoconfigure-2.7.5.jar
    |-- spring-boot-starter-2.7.5.jar
    |-- spring-boot-starter-jdbc-2.7.5.jar
    |-- spring-boot-starter-json-2.7.5.jar
    |-- spring-boot-starter-log4j2-2.7.5.jar
    |-- spring-boot-starter-tomcat-2.7.5.jar
    |-- spring-boot-starter-web-2.7.5.jar
    |-- spring-context-5.3.23.jar
    |-- spring-core-5.3.23.jar
    |-- spring-expression-5.3.23.jar
    |-- spring-jcl-5.3.23.jar
    |-- spring-jdbc-5.3.23.jar
    |-- spring-tx-5.3.23.jar
    |-- spring-web-5.3.23.jar
    |-- spring-webmvc-5.3.23.jar
    |-- ssl-config-core_2.12-0.3.7.jar
    |-- stax2-api-3.1.4.jar
    |-- token-provider-1.0.1.jar
    |-- tomcat-embed-core-9.0.68.jar
    |-- tomcat-embed-el-9.0.68.jar
    |-- tomcat-embed-websocket-9.0.68.jar
    |-- velocity-engine-core-2.3.jar
    |-- woodstox-core-5.0.3.jar
    |-- zlink-0.1.0.jar
    |-- zlink-admin-0.1.0.jar
    |-- zlink-cdc-1.13-0.1.0.jar
    |-- zlink-common-0.1.0.jar
    |-- zlink-flink-1.13-0.1.0.jar
    |-- zlink-metadata-base-0.1.0.jar
    |-- zlink-metadata-mysql-0.1.0.jar
    |-- zlink-metadata-postgresql-0.1.0.jar
    `-- zookeeper-3.4.13.jar
```

### 配置文件

修改配置文件，主要是修改数据库项目的东西

```shell
cd zlink-release-{version}/config
vim application.yml
```

## 数据库初始化

首先创建 `zlink `  数据库

```sql
CREATE DATABASE `zlink` CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci'
```

然后在数据库中执行 `zlink-release-{version}/config/db/zlink.sql` 文件中的内容

## 启停 zlink

```shell
#启动
./zlink.sh start
#停止
./zlink.sh stop
```
