package com.github.vshushkov.balancer;

import com.github.terma.javaniotcpserver.TcpServer;
import com.github.terma.javaniotcpserver.TcpServerConfig;

import java.util.List;

/**
 * @author v_shushkov
 */
public class Balancer {
    private final TcpServer server;

    public Balancer(final List<BalancerItem> balancerItems, int port) {
        RoundRobinHandlerFactory handlerFactory = new RoundRobinHandlerFactory(balancerItems);

        /**
         * @todo add multi-thread support
         */
        int workersCount = 1;

        server = new TcpServer(new TcpServerConfig(port, handlerFactory, workersCount));
    }

    public void start() {
        server.start();
    }

    public void shutdown() {
        server.shutdown();
    }
}
