package org.acme.services;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import io.quarkus.arc.DefaultBean;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.SseEventSink;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
@ApplicationScoped
@DefaultBean
public class HazelcastMapService implements IMapService {

    private IMap<String, SseEventSink> map;
    private final HazelcastInstance hazelcastInstance;

    @Inject
    public HazelcastMapService(@ConfigProperty(name="cluster.name") String clusterName){
        Log.info("Connecting to:" + clusterName);
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress(clusterName);
        this.hazelcastInstance = HazelcastClient.newHazelcastClient(config);
    }

    @Override
    public void put(String key, SseEventSink sseEventSink) {

        if(map == null) {
            map = this.hazelcastInstance.getMap("my-distributed-map");
        }

        map.put(key, sseEventSink);

    }

    @Override
    public SseEventSink get(String key) {

        if(map == null) {
            map = this.hazelcastInstance.getMap("my-distributed-map");
        }

        return map.get(key);
    }

    @Override
    public void clean() {
        if(map != null) {
            Log.info("Not null");
            List<String> list = new ArrayList<>();
            // Iterating over map entries
            for (IMap.Entry<String, SseEventSink> entry : map.entrySet()) {
                if(entry.getValue().isClosed()){
                    list.add(entry.getKey());
                }
            }

            for(String key: list){
                map.remove(key);
            }
        }
    }


}
