package io.github.shoothzj.http.facade.core;

import lombok.Setter;

@Setter
public class TlsConfig {
    private String keyStorePath;

    private char[] keyStorePassword;

    private String trustStorePath;

    private char[] trustStorePassword;

    private boolean verifyDisabled;

    private boolean hostnameVerifyDisabled;

    private String[] versions;

    private String[] cipherSuites;

    private TlsConfig() {
    }

    public String keyStorePath() {
        return keyStorePath;
    }

    public char[] keyStorePassword() {
        return keyStorePassword;
    }

    public String trustStorePath() {
        return trustStorePath;
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

    @Override
    public String toString() {
        return "TlsConfig{\n" + "verifyDisabled=" + verifyDisabled + "\n" + "hostnameVerifyDisabled=" + hostnameVerifyDisabled + "\n" + "}";
    }
}
