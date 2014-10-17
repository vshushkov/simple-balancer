package com.github.vshushkov.dummyserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author v_shushkov
 */
public class DummyClient {

    private final static Logger logger = Logger.getAnonymousLogger();

    public String getResponse(String host, int port) throws IOException {

        StringBuilder response = new StringBuilder();

        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));

        Selector selector = Selector.open();
        channel.register(selector, SelectionKey.OP_CONNECT);

        try {
            while (selector.select(100) > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();

                    if ((key.isValid()) || (key.isConnectable())) {
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }

                        if (key.isConnectable()) {
                            // connected, start writing...
                            ByteBuffer serverBuffer = ByteBuffer.wrap("request".getBytes());
                            channel.write(serverBuffer);
                            channel.register(selector, SelectionKey.OP_READ);
                        }

                        if (key.isReadable()) {
                            // ... receive response from server

                            // writing to the buffer
                            ByteBuffer clientBuffer = ByteBuffer.allocateDirect(1024);

                            int length = channel.read(clientBuffer);

                            if (length >= 0) {
                                // switch buffer to read mode
                                clientBuffer.flip();

                                // read from buffer
                                response.append(StandardCharsets.UTF_8.decode(clientBuffer));
                            } else {
                                channel.close();
                            }
                        }

                    }
                }
            }

        } catch (IOException e) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Error in client", e);
            }
        }

        return response.toString();
    }
}
