package znurgl.ignitetest.worker;

import com.sun.tools.javac.util.List;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.transactions.Transaction;
import znurgl.ignitetest.data.Event;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Gergo Bakos (znurgl@gmail.com) on 18/07/2018.
 */
public class Worker {

    public void load() {

        IgniteConfiguration cfg = new IgniteConfiguration();
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

        TcpDiscoveryMulticastIpFinder ipfinder = new TcpDiscoveryMulticastIpFinder();
        ipfinder.setAddresses(List.of("127.0.0.1:47500..47509"));

        discoverySpi.setIpFinder(ipfinder);

        cfg.setDiscoverySpi(discoverySpi);

        try (Ignite ignite = Ignition.getOrStart(cfg)) {

            CacheConfiguration<UUID, Event> cacheCfg = new CacheConfiguration<>("transtest");


            try (IgniteCache<UUID, Event> cache = ignite.getOrCreateCache(cacheCfg)) {
                try (Transaction tx = ignite.transactions().txStart()) {

                    Map<UUID, Event> map = new ConcurrentHashMap<>();
                    for (int i = 0; i < 1_000_000; i++) {
                        map.put(UUID.randomUUID(), new Event("name" + i, Timestamp.valueOf(LocalDateTime.now()).getTime(), ""));
                    }

                    ignite.log().info("cache size before: " + cache.metrics().getSize());
                    cache.putAll(map);
                    tx.commit();
                    ignite.log().info("cache size after: " + cache.metrics().getSize());
                }
            }
        }
    }
}
