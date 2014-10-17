package com.github.vshushkov.dummyserver;

import com.github.terma.javaniotcpserver.TcpServer;
import com.github.terma.javaniotcpserver.TcpServerConfig;

/**
 * @author v_shushkov
 */
public class DummyServer {
    private final TcpServer server;

    public DummyServer(int port, String responseWith) {
        DummyServerHandlerFactory handlerFactory = new DummyServerHandlerFactory(responseWith);
        server = new TcpServer(new TcpServerConfig(port, handlerFactory, 1));
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }
}
