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

import lombok.Setter;

@Setter
public class TlsConfig {
    private String keyStorePath;

    private byte[] keyStoreContent;

    private char[] keyStorePassword;

    private String trustStorePath;

    private byte[] trustStoreContent;

    private char[] trustStorePassword;

    private boolean verifyDisabled;

    private boolean hostnameVerifyDisabled;

    private String[] versions;

    private String[] cipherSuites;

    private TlsConfig(Builder builder) {
        this.keyStorePath = builder.keyStorePath;
        this.keyStoreContent = builder.keyStoreContent;
        this.keyStorePassword = builder.keyStorePassword;
        this.trustStorePath = builder.trustStorePath;
        this.trustStoreContent = builder.trustStoreContent;
        this.trustStorePassword = builder.trustStorePassword;
        this.verifyDisabled = builder.verifyDisabled;
        this.hostnameVerifyDisabled = builder.hostnameVerifyDisabled;
        this.versions = builder.versions;
        this.cipherSuites = builder.cipherSuites;
    }

    public String keyStorePath() {
        return keyStorePath;
    }

    public byte[] keyStoreContent() {
        return keyStoreContent;
    }

    public char[] keyStorePassword() {
        return keyStorePassword;
    }

    public String trustStorePath() {
        return trustStorePath;
    }

    public byte[] trustStoreContent() {
        return trustStoreContent;
    }

    public char[] trustStorePassword() {
        return trustStorePassword;
    }

    public boolean verifyDisabled() {
        return verifyDisabled;
    }

    public boolean hostnameVerifyDisabled() {
        return hostnameVerifyDisabled;
    }

    public String[] versions() {
        return versions;
    }

    public String[] cipherSuites() {
        return cipherSuites;
    }

    public static class Builder {
        private String keyStorePath;

        private byte[] keyStoreContent;

        private char[] keyStorePassword;

        private String trustStorePath;

        private byte[] trustStoreContent;

        private char[] trustStorePassword;

        private boolean verifyDisabled;

        private boolean hostnameVerifyDisabled;

        private String[] versions;

        private String[] cipherSuites;

        public Builder keyStore(String keyStorePath, char[] keyStorePassword) {
            this.keyStorePath = keyStorePath;
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public Builder keyStore(byte[] keyStoreContent, char[] keyStorePassword) {
            this.keyStoreContent = keyStoreContent;
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public Builder trustStore(String trustStorePath, char[] trustStorePassword) {
            this.trustStorePath = trustStorePath;
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public Builder trustStore(byte[] trustStoreContent, char[] trustStorePassword) {
            this.trustStoreContent = trustStoreContent;
            this.trustStorePassword = trustStorePassword;
            return this;
        }

        public Builder verifyDisabled(boolean verifyDisabled) {
            this.verifyDisabled = verifyDisabled;
            return this;
        }

        public Builder hostnameVerifyDisabled(boolean hostnameVerifyDisabled) {
            this.hostnameVerifyDisabled = hostnameVerifyDisabled;
            return this;
        }

        public Builder versions(String[] versions) {
            this.versions = versions;
            return this;
        }

        public Builder cipherSuites(String[] cipherSuites) {
            this.cipherSuites = cipherSuites;
            return this;
        }

        public TlsConfig build() {
            return new TlsConfig(this);
        }
    }

    @Override
    public String toString() {
        return "TlsConfig{\n" + "verifyDisabled=" + verifyDisabled + "\n" + "hostnameVerifyDisabled=" + hostnameVerifyDisabled + "\n" + "}";
    }
}
