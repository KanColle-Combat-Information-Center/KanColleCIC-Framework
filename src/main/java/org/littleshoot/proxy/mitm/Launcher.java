package org.littleshoot.proxy.mitm;

import java.io.File;

import org.littleshoot.proxy.HttpProxyServerBootstrap;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

    private static final Logger LOG = LoggerFactory.getLogger(Launcher.class);

    public static void main(final String... args) {
        try {
            final int port = 1823;

            System.out.println("About to start server on port: " + port);
            HttpProxyServerBootstrap bootstrap = DefaultHttpProxyServer
                    .bootstrapFromFile("./littleproxy.properties")
                    .withPort(port).withAllowLocalOnly(false);

            bootstrap.withManInTheMiddle(new CertificateSniffingMitmManager());

            System.out.println("About to start...");
            bootstrap.start();

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            System.exit(1);
        }
    }

}
