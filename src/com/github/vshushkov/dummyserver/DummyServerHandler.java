package com.github.vshushkov.dummyserver;

import com.github.terma.javaniotcpserver.TcpServerHandler;
import com.github.vshushkov.balancer.BalancerItem;
import com.github.vshushkov.balancer.TcpProxyBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author v_shushkov
 */
public class DummyServerHandler implements TcpServerHandler {

    private final static Logger logger = Logger.getAnonymousLogger();

    private final SocketChannel clientChannel;
    private final ByteBuffer buffer = ByteBuffer.allocate(1000);

    public DummyServerHandler(SocketChannel clientChannel, String responseWith) {
        this.clientChannel = clientChannel;
        buffer.put(responseWith.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
    }

    @Override
    public void register(Selector selector) {
        try {
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_WRITE, this);
        } catch (final IOException exception) {
            destroy();
        }
    }

    @Override
    public void process(final SelectionKey key) {
        try {
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
            }
            clientChannel.register(key.selector(), 0, this);
        } catch (final ClosedChannelException exception) {
            destroy();
        } catch (final IOException exception) {
            destroy();
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Could not process.", exception);
            }
        }
    }

    @Override
    public void destroy() {
        try {
            clientChannel.close();
        } catch (IOException exception) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Could not close channel properly.", exception);
            }
        }
    }
}
