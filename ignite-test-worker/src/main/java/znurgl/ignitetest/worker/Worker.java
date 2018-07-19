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
    
    public static void main(String[] args) {
        new Worker().load();
    }

    public void load() {

        IgniteConfiguration cfg = new IgniteConfiguration();

        cfg.setClientMode(true);

        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

        //discoverySpi.setForceServerMode(false);

        TcpDiscoveryMulticastIpFinder ipfinder = new TcpDiscoveryMulticastIpFinder();
        ipfinder.setAddresses( Arrays.asList("127.0.0.1:47500..47509") );

        discoverySpi.setIpFinder(ipfinder);

        cfg.setDiscoverySpi(discoverySpi);
        System.out.println("start");
        try(Ignite ignite = Ignition.getOrStart(cfg)) {
       
            IgniteTransactions transactions = ignite.transactions();

            
            CacheConfiguration<UUID, Event> cacheCfg = new CacheConfiguration<>("transtest");

            try(IgniteCache<UUID, Event> cache = ignite.getOrCreateCache(cacheCfg)) {
                Map<UUID, Event> map = new ConcurrentHashMap<>();
                try (Transaction tx = transactions.txStart()) {
                   
                    for (int i = 0; i < 1000; i++) {
                        map.put(UUID.randomUUID(), new Event("name" + i, Timestamp.valueOf(LocalDateTime.now()).getTime(), ""));
                    }

                    //System.out.println(cache.metrics().getSize());
                    cache.putAll(map);

                    tx.commit();

                    //System.out.println(cache.metrics().getSize());
                }
                Map<UUID, Event> resp = cache.getAll(map.keySet());
                System.out.println("loaded: " + resp.size());
            }
        }
    }
}
