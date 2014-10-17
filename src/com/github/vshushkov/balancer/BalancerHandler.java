package com.github.vshushkov.balancer;

import com.github.terma.javaniotcpserver.TcpServerHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @see @link https://github.com/terma/java-nio-tcp-proxy/blob/master/src/com/github/terma/javaniotcpproxy/TcpProxyConnector.java
 */
public class BalancerHandler implements TcpServerHandler {

    private final static Logger logger = Logger.getAnonymousLogger();

    private final TcpProxyBuffer clientBuffer = new TcpProxyBuffer();
    private final TcpProxyBuffer serverBuffer = new TcpProxyBuffer();
    private final SocketChannel clientChannel;

    private Selector selector;
    private SocketChannel serverChannel;
    private BalancerItem balancerItem;

    public BalancerHandler(SocketChannel clientChannel, BalancerItem balancerItem) {
        this.clientChannel = clientChannel;
        this.balancerItem = balancerItem;
    }

    public void readFromClient() throws IOException {
        serverBuffer.writeFrom(clientChannel);
        if (serverBuffer.isReadyToRead()) {
            register();
        }
    }

    public void readFromServer() throws IOException {
        clientBuffer.writeFrom(serverChannel);
        if (clientBuffer.isReadyToRead()) {
            register();
        }
    }

    public void writeToClient() throws IOException {
        clientBuffer.writeTo(clientChannel);
        if (clientBuffer.isReadyToWrite()) {
            register();
        }
    }

    public void writeToServer() throws IOException {
        serverBuffer.writeTo(serverChannel);
        if (serverBuffer.isReadyToWrite()) {
            register();
        }
    }

    public void register() throws ClosedChannelException {
        int clientOps = 0;
        if (serverBuffer.isReadyToWrite()) {
            clientOps |= SelectionKey.OP_READ;
        }
        if (clientBuffer.isReadyToRead()) {
            clientOps |= SelectionKey.OP_WRITE;
        }
        clientChannel.register(selector, clientOps, this);

        int serverOps = 0;
        if (clientBuffer.isReadyToWrite()) {
            serverOps |= SelectionKey.OP_READ;
        }
        if (serverBuffer.isReadyToRead()) {
            serverOps |= SelectionKey.OP_WRITE;
        }
        serverChannel.register(selector, serverOps, this);
    }

    private static void closeQuietly(SocketChannel channel) {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException exception) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.log(Level.WARNING, "Could not close channel properly.", exception);
                }
            }
        }
    }

    @Override
    public void register(Selector selector) {
        this.selector = selector;

        try {
            clientChannel.configureBlocking(false);

            final InetSocketAddress socketAddress =
                new InetSocketAddress(balancerItem.getHost(), balancerItem.getPort());

            serverChannel = SocketChannel.open();
            serverChannel.connect(socketAddress);
            serverChannel.configureBlocking(false);

            register();
        } catch (final IOException exception) {
            destroy();

            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Could not connect to " + balancerItem.getHost() + ":" + balancerItem.getPort(), exception);
            }
        }
    }

    /**
     *
     *
     * @param key
     */
    @Override
    public void process(final SelectionKey key) {
        try {
            if (key.channel() == clientChannel) {
                if (key.isValid() && key.isReadable()) readFromClient();
                if (key.isValid() && key.isWritable()) writeToClient();
            }

            if (key.channel() == serverChannel) {
                if (key.isValid() && key.isReadable()) readFromServer();
                if (key.isValid() && key.isWritable()) writeToServer();
            }
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
        closeQuietly(clientChannel);
        closeQuietly(serverChannel);
    }
}
