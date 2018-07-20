package znurgl.ignitetest.worker;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.transactions.Transaction;
import znurgl.ignitetest.data.Event;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Gergo Bakos (znurgl@gmail.com) on 18/07/2018.
 */
public class Worker {

    private static String mIgniteConfigFile;
    private static String cacheName;
    private static Integer load;

    public static void main(String[] args) {
        mIgniteConfigFile = args[0];
        cacheName =  args[1];
        load = Integer.valueOf(args[2]);
        new Worker().load();
    }

    public void load() {

        try(Ignite ignite = Ignition.start(mIgniteConfigFile)) {

            IgniteTransactions transactions = ignite.transactions();


            CacheConfiguration<UUID, Event> cacheCfg = new CacheConfiguration<>(cacheName);

            try(IgniteCache<UUID, Event> cache = ignite.getOrCreateCache(cacheCfg)) {
                Map<UUID, Event> map = new ConcurrentHashMap<>();
                try (Transaction tx = transactions.txStart()) {

                    for (int i = 0; i < load; i++) {
                        map.put(UUID.randomUUID(), new Event("name" + i, Timestamp.valueOf(LocalDateTime.now()).getTime(), ""));
                    }

                    cache.putAll(map);
                    tx.commit();

                }
                Map<UUID, Event> resp = cache.getAll(map.keySet());
                System.out.println("loaded: " + resp.size());
            }
        }
    }
}
