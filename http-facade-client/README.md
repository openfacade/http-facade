# HttpClientFacade

`HttpClientFacade` is a flexible and extensible Java HTTP client library that supports multiple underlying HTTP engines (e.g., OkHttp, AsyncHttpClient, and JDK's `HttpClient`). It provides both asynchronous (default) and synchronous methods for making HTTP requests, along with easy configuration of connection timeouts, TLS settings, and other HTTP client parameters. The library is designed to be lightweight and configurable while offering a unified API to interact with various HTTP clients.

## Features

- **Support for Multiple HTTP Engines**:
    - `AsyncHttpClient`
    - `JDK HttpClient`
    - `OkHttp`

## Installation

Add the following dependency to your `pom.xml` if you're using Maven:

```xml
<dependency>
    <groupId>io.github.shoothzj</groupId>
    <artifactId>http-facade-client</artifactId>
    <version>${http-facade-client.version}</version>
</dependency>
```

Or with Gradle:

```groovy
implementation 'io.github.shoothzj:http-facade-client:$httpClientFacadeVersion'
```

## Getting Started

### 1. Create a Configuration

First, create a `HttpClientConfig` to configure timeouts and TLS settings.

```java
HttpClientConfig config = new HttpClientConfig.Builder()
    .engine(HttpClientEngine.OKHTTP) // Choose the engine (e.g., OKHTTP, ASYNC_HTTP_CLIENT, JDK)
    .timeout(Duration.ofSeconds(30)) // Set request timeout
    .connectTimeout(Duration.ofSeconds(10)) // Set connection timeout
    .build();
```

### 2. Create a Client

Use the `HttpClientFactory` to create the desired client instance with the above configuration.

```java
HttpClient client = HttpClientFactory.createHttpClient(config);
```

### 3. Make HTTP Requests

#### Asynchronous Requests

```java
CompletableFuture<HttpResponse> future = client.get("http://example.com", Map.of());
future.thenAccept(response -> {
    System.out.println("Status: " + response.getStatusCode());
    System.out.println("Body: " + new String(response.getBody()));
});
```

#### Synchronous Requests

```java
HttpResponse response = client.getSync("http://example.com", Map.of());
System.out.println("Status: " + response.getStatusCode());
System.out.println("Body: " + new String(response.getBody()));
```

### 4. Close the Client

Remember to close the client when you're done to release any resources.

```java
client.close();
```
