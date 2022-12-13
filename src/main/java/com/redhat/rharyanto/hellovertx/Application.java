package com.redhat.rharyanto.hellovertx;

import com.hazelcast.config.Config;
import com.redhat.rharyanto.hellovertx.util.BannerUtil;
import com.redhat.rharyanto.hellovertx.util.PropertiesUtil;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.io.IOException;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static final String APP_NAME = "hello-vertx";

    public static void main(String[] args) {

        // Enable Log4J2 implementation instead
        InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);

        // Specify HZ mode, either 'dev' or 'file'
        // 'dev' is multicast implementation of embedded HZ
        // 'file' is file configuration based implementation of embedded HZ
        String hzMode = (System.getProperty("hazelcast.mode") != null) ? System.getProperty("hazelcast.mode") : "dev";

        ClusterManager hzMgr = null;
        if (hzMode.equalsIgnoreCase("dev")) {
            Config hazelcastConfig = new Config();
            hazelcastConfig.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1").setEnabled(true);
            hazelcastConfig.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
            hazelcastConfig.setClusterName(APP_NAME + "-dev");

            hzMgr = new HazelcastClusterManager(hazelcastConfig);

        } else if (hzMode.equalsIgnoreCase("file")) {
            hzMgr = new HazelcastClusterManager();
        }

        VertxOptions options = new VertxOptions().setClusterManager(hzMgr);
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
                configRetriever.getConfig(json -> {
                    try {
                        BannerUtil.show(PropertiesUtil.getConfig(json.result(), "banner.file", null));

                        logger.info("Starting the application...");
                        vertx.deployVerticle(new MainVerticle(json.result()));

                    } catch (IOException ioe) {
                        logger.error("Not able to retrieve banner file!");
                    }
                });
            } else {
                // failed!
            }
        });
    }
}
