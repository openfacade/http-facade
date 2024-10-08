package io.github.openfacade.http;


import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtil {
    /**
     * typical used in unit test, so exception is not big deal
     * @return available port
     */
    public static int findAvailablePort() {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            throw new RuntimeException("Failed to find available port", e);
        }
    }
}
