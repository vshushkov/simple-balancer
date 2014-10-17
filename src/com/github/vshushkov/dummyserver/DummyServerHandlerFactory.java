package com.github.vshushkov.dummyserver;

import com.github.terma.javaniotcpserver.TcpServerHandler;
import com.github.terma.javaniotcpserver.TcpServerHandlerFactory;
import java.nio.channels.SocketChannel;

/**
 * @author v_shushkov
 */
public class DummyServerHandlerFactory implements TcpServerHandlerFactory {
    private final String responseWith;

    public DummyServerHandlerFactory(String responseWith) {
        this.responseWith = responseWith;
    }

    @Override
    public TcpServerHandler create(final SocketChannel clientChannel) {
        return new DummyServerHandler(clientChannel, responseWith);
    }
}
