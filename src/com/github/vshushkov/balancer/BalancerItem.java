package com.github.vshushkov.balancer;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author v_shushkov
 */
public class BalancerItem {

    private final static Logger logger = Logger.getAnonymousLogger();

    final private String host;
    final private int port;

    public BalancerItem(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return getHost() +":"+ getPort();
    }

    public static class Parser {

        public static List<BalancerItem> parse(String string) {
            if (Strings.isNullOrEmpty(string)) {
                throw new IllegalArgumentException("Input string is empty");
            }

            List<String> wrongHosts = new ArrayList<>();
            List<BalancerItem> balancerItems = new ArrayList<>();

            for (String part : string.trim().split(",")) {
                try {
                    URL url = new URL("http://"+ part);
                    int port = url.getPort();
                    if (port == -1) {
                        port = 80;
                    }
                    BalancerItem balancerItem = new BalancerItem(url.getHost(), port);
                    balancerItems.add(balancerItem);
                    if (logger.isLoggable(Level.INFO)) {
                        logger.log(Level.INFO, "Found host "+ balancerItem.toString());
                    }
                } catch (MalformedURLException e) {
                    wrongHosts.add(part);
                }
            }

            if (!wrongHosts.isEmpty()) {
                throw new IllegalArgumentException("Wrong hosts: "+ Joiner.on(", ").join(wrongHosts));
            }

            return balancerItems;
        }

    }
}
