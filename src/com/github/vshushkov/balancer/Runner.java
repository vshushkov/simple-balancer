package com.github.vshushkov.balancer;

import java.io.IOException;
import java.util.List;

public class Runner {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Please, specify comma-separated hosts list!");
            System.exit(1);
        }

        List<BalancerItem> balancerItems = BalancerItem.Parser.parse(args[0]);

        int port = 3001;
        try {
            if (args.length == 2) {
                port = Integer.valueOf(args[1]);
            }
        } catch (NumberFormatException e) {
            System.err.println("Balancer's port value is wrong!");
            System.exit(1);
        }

        new Balancer(balancerItems, port).start();
    }
}
