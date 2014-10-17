package com.github.vshushkov.balancer;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author v_shushkov
 */
public class BalancerItemParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void emptyConfigString() {
        BalancerItem.Parser.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongConfigString() {
        BalancerItem.Parser.parse("host:wrong-port,host:10");
    }

    @Test
    public void correctConfigString() {
        List<BalancerItem> items = BalancerItem.Parser.parse("host:1,host:2,another-host:3,another-host");

        assertEquals(4, items.size());

        assertEquals("host", items.get(0).getHost());
        assertEquals(1, items.get(0).getPort());

        assertEquals("host", items.get(1).getHost());
        assertEquals(2, items.get(1).getPort());

        assertEquals("another-host", items.get(2).getHost());
        assertEquals(3, items.get(2).getPort());

        assertEquals("another-host", items.get(3).getHost());
        assertEquals(80, items.get(3).getPort());
    }

}
