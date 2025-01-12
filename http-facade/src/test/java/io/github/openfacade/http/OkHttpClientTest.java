/*
 * Copyright 2024 OpenFacade Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.openfacade.http;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

class OkHttpClientTest {

    @Test
    void testConstructorWithIllegalMaxIdleConnections() throws Exception {
        HttpClientConfig.OkHttpConfig okHttpConfig = new HttpClientConfig.OkHttpConfig();
        HttpClientConfig.OkHttpConfig.ConnectionPoolConfig poolConfig =
                new HttpClientConfig.OkHttpConfig.ConnectionPoolConfig();
        poolConfig.setMaxIdleConnections(-1);
        poolConfig.setKeepAliveDuration(Duration.ofSeconds(10));
        okHttpConfig.setConnectionPoolConfig(poolConfig);

        HttpClientConfig clientConfig = new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp)
                .okHttpConfig(okHttpConfig)
                .build();
        try (OkHttpClient ignored = new OkHttpClient(clientConfig)) {
            fail("Should throw IllegalArgumentException for illegal maxIdleConnections");
        } catch (Exception e) {
            assertInstanceOf(IllegalArgumentException.class, e);
            assertEquals("maxIdleConnections should not be negative.", e.getMessage());
        }
    }

    @Test
    void testConstructorWithIllegalKeepAliveDuration() throws Exception {
        HttpClientConfig.OkHttpConfig okHttpConfig = new HttpClientConfig.OkHttpConfig();
        HttpClientConfig.OkHttpConfig.ConnectionPoolConfig poolConfig =
                new HttpClientConfig.OkHttpConfig.ConnectionPoolConfig();
        poolConfig.setMaxIdleConnections(1);
        poolConfig.setKeepAliveDuration(Duration.ofMillis(-100));
        okHttpConfig.setConnectionPoolConfig(poolConfig);

        HttpClientConfig clientConfig = new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp)
                .okHttpConfig(okHttpConfig)
                .build();

        try (OkHttpClient ignored = new OkHttpClient(clientConfig)) {
            fail("Should throw IllegalArgumentException for illegal keepAliveDuration");
        } catch (Exception e) {
            assertInstanceOf(IllegalArgumentException.class, e);
            assertEquals("keepAliveDuration should not be null or negative.", e.getMessage());
        }
    }
}
