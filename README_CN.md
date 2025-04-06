# HTTP Facade

![License](https://img.shields.io/badge/开源许可证-Apache2.0-green) ![language](https://img.shields.io/badge/语言-Java-blue.svg) [![version](https://img.shields.io/github/v/tag/opengemini/opengemini-client-java?label=%e5%8f%91%e8%a1%8c%e7%89%88%e6%9c%ac&color=blue)](https://github.com/opengemini/opengemini-client-java/releases)

[English](README.md) | 简体中文

`HTTP Facade` 是一个灵活的 Java 库，为 HTTP 客户端和 HTTP 服务器（开发中）提供了统一的 API。该库支持多种底层实现（例如，客户端的 OkHttp 和 AsyncHttpClient），并且提供了 HTTP 请求和响应的便捷配置、连接超时、TLS 设置、路由等功能。

## 为什么使用 Facade 库？

`HTTP Facade` 库为在单个应用中集成多种 HTTP 客户端实现提供了一种精简的方式。通过提供统一的 API，它简化了适配各种 HTTP 引擎（如 `OkHttp` 或 `AsyncHttpClient`）的复杂性，同时允许便捷地配置关键参数，例如请求超时、TLS 设置。

在许多集成场景中，基于 HTTP 库的应用可能需要适应特定的需求。例如，一些嵌入式环境优先考虑更小的内存占用，Android 应用则需要特定的兼容性，而其他场景可能需要高性能和低延迟。在这些情况下，使用一层轻薄的 facade 门面非常地有优势，使开发者能够满足多种需求而无需重写核心逻辑或管理多个客户端库。`HTTP Facade` 使得不同实现之间的切换更加轻松，确保了在各种使用场景下的一致性、简洁性和更高的可维护性。

## HttpClientFacade

### 支持的Http Client引擎

- [**AsyncHttpClient**](https://github.com/AsyncHttpClient/async-http-client)
- **JavaHttpClient**: Java11+ built-in HTTP client
- **Java8HttpClient**: Java8 built-in HTTP client
- [**JettyHttpClient**](https://github.com/jetty/jetty.project)
- [**OkHttpClient**](https://github.com/square/okhttp)
- [**VertxHttpClient**](https://github.com/vert-x3/vertx-web)

### 安装

默认情况下，`HttpClientFacade` 使用 JDK 提供的内置 HTTP 引擎。它会根据 Java 版本自动选择合适的实现，支持 Java 8 和 Java 11+。不过，如果您希望使用特定的 HTTP 引擎（如 `OkHttp` 或 `AsyncHttpClient`），您需要添加相应的依赖。

#### Maven

在 `pom.xml` 中添加以下依赖项，以包含默认的 `http-facade`：

```xml
<dependency>
    <groupId>io.github.openfacade</groupId>
    <artifactId>http-facade</artifactId>
    <version>${http-facade.version}</version>
</dependency>
```

如果您希望使用特定的 HTTP 引擎，可以添加以下附加依赖：

- **AsyncHttpClient**：

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>org.asynchttpclient</groupId>
      <artifactId>async-http-client</artifactId>
      <version>${asynchttp.version}</version>
  </dependency>
  ```

- **OkHttp**：

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>com.squareup.okhttp3</groupId>
      <artifactId>okhttp</artifactId>
      <version>${okhttp.version}</version>
  </dependency>
  ```

#### Gradle

对于 Gradle 用户，添加默认的 `http-facade` 依赖：

```groovy
implementation 'io.github.openfacade:http-facade-client:$httpFacadeVersion'
```

要指定 HTTP 引擎，添加以下附加依赖：

- **AsyncHttpClient**：

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'org.asynchttpclient:async-http-client:$asynchttpVersion'
  ```

- **OkHttp**：

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'com.squareup.okhttp3:okhttp:$okhttpVersion'
  ```

### 入门

#### 1. 创建配置

首先，创建 `HttpClientConfig` 来配置超时和 TLS 设置。

```java
HttpClientConfig config = new HttpClientConfig.Builder()
    .engine(HttpClientEngine.OKHTTP) // 选择引擎（如 OKHTTP, ASYNC_HTTP_CLIENT, JDK）
    .timeout(Duration.ofSeconds(30)) // 设置请求超时
    .connectTimeout(Duration.ofSeconds(10)) // 设置连接超时
    .build();
```

#### 2. 创建客户端

使用 `HttpClientFactory` 创建具有上述配置的客户端实例。

```java
HttpClient client = HttpClientFactory.createHttpClient(config);
```

#### 3. 发送 HTTP 请求

##### 异步请求

```java
CompletableFuture<HttpResponse> future = client.get("http://example.com", Map.of());
future.thenAccept(response -> {
    System.out.println("Status: " + response.getStatusCode());
    System.out.println("Body: " + new String(response.getBody()));
});
```

##### 同步请求

```java
HttpResponse response = client.getSync("http://example.com", Map.of());
System.out.println("Status: " + response.getStatusCode());
System.out.println("Body: " + new String(response.getBody()));
```

#### 4. 关闭客户端

完成操作后，请记得关闭客户端以释放资源。

```java
client.close();
```
test cla ci
