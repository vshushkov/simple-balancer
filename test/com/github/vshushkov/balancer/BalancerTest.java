package com.github.vshushkov.balancer;

import com.github.vshushkov.dummyserver.DummyClient;
import com.github.vshushkov.dummyserver.DummyServer;

import com.google.common.base.Joiner;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author v_shushkov
 */
public class BalancerTest {

    @Test
    public void test() throws IOException {

        DummyServer host1 = new DummyServer(9002, "1");
        host1.start();
        DummyServer host2 = new DummyServer(9003, "2");
        host2.start();
        DummyServer host3 = new DummyServer(9004, "3");
        host3.start();

        List<BalancerItem> balancerItems =
            BalancerItem.Parser.parse("localhost:9002,localhost:9003,localhost:9004");

        String balancerHost = "localhost";
        int balancerPort = 9001;
        Balancer balancer = new Balancer(balancerItems, balancerPort);
        balancer.start();

        List<String> responses = new ArrayList<>();

        DummyClient client = new DummyClient();

        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));
        responses.add(client.getResponse(balancerHost, balancerPort));

        assertEquals("1 2 3 1 2 3 1 2 3 1 2 3 1 2", Joiner.on(" ").join(responses));
    }

}
