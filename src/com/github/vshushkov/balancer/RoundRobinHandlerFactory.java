package com.github.vshushkov.balancer;

import com.github.terma.javaniotcpserver.TcpServerHandler;
import com.github.terma.javaniotcpserver.TcpServerHandlerFactory;

import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author v_shushkov
 */
public class RoundRobinHandlerFactory implements TcpServerHandlerFactory {
    private final List<BalancerItem> balancerItems;
    private int cursor = 0;

    public RoundRobinHandlerFactory(List<BalancerItem> balancerItems) {
        this.balancerItems = balancerItems;
    }

    private BalancerItem nextItem() {
        if (cursor >= this.balancerItems.size()) {
            cursor = 0;
        }
        return this.balancerItems.get(cursor++);
    }

    @Override
    public TcpServerHandler create(final SocketChannel clientChannel) {
        return new BalancerHandler(clientChannel, nextItem());
    }
}
